import java.io.*;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class WordCount extends Configured implements Tool {

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

		static enum Counters {
			INPUT_WORDS
		}

		private final static IntWritable one = new IntWritable(1);
		private Text word_collection = new Text();

		private boolean case_sensitive = true;
		private Set<String> patterns_to_skip = new HashSet<String>();

		private long repetitions = 0;
		private String input_file;

		public void configure(JobConf job) {
			case_sensitive = job.getBoolean("wordcount.case.sensitive", true);
			input_file = job.get("map.input.file");

			if (job.getBoolean("wordcount.skip.patterns", false)) {
				Path[] pattern_files = new Path[0];
				try {
					pattern_files = DistributedCache.getLocalCacheFiles(job);
				} catch (IOException ioe) {
					System.err.println(
							"Caught exception while getting cached files: " + StringUtils.stringifyException(ioe));
				}
				for (Path patternsFile : pattern_files) {
					skipFile(patternsFile);
				}
			}
		}

		private void skipFile(Path patternsFile) {
			try {
				BufferedReader fis = new BufferedReader(new FileReader(patternsFile.toString()));
				String pattern = null;
				while ((pattern = fis.readLine()) != null) {
					patterns_to_skip.add(pattern);
				}
			} catch (IOException ioe) {
				System.err.println("Caught exception while parsing the cached file '" + patternsFile + "' : "
						+ StringUtils.stringifyException(ioe));
			}
		}

		public String getMin(String a, String b) {
			return a.compareTo(b) > 0 ? b : a;
		}

		public String getMax(String a, String b) {
			return a.compareTo(b) > 0 ? a : b;
		}

		public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			String row = (case_sensitive) ? value.toString() : value.toString().toLowerCase();
			String[] unimportant_words = { "a", "what", "your", "you", "is", "an", "the", "some", "my", "i", "not", "am", "as", "so",
			"because", "even", "if", "of", "how", "new", "do", "once", "only", "than", "that", "though", "until", "when",
			"whenever", "where", "while", "and","either", "or", "neither", "with", "at",
			"from", "into", "during", "until", "among", "ask", "hn", "upon", "to", "in", "for", "on", "by",
			"about", "like", "through", "over", "before", "between", "after", "but" };

			for (int i = 33; i < 256; i++) {
				if ((i > 64 && i < 91) || (i > 96 && i < 123)) {
					char ascii_char = (char) i;
					String important_words = " " + ascii_char + " ";

					row = row.replaceAll(important_words, " ");
				} else if (i > 47 && i < 58) {
					char ascii_char = (char) i;
					String important_words = "" + ascii_char;

					row = row.replaceAll(important_words, "");
				} else {
					char ascii_char = (char) i;
					String important_words = "\\" + ascii_char;

					row = row.replaceAll(important_words, "");
				}
			}

			for (int i = 0; i < unimportant_words.length; i++) {
				row = row.replaceAll(" " + unimportant_words[i] + " ", " ");
			}

			String[] row_wordset_test = row.split("\\s");
			int deleted = 0;

			for (int i = 0; i < unimportant_words.length; i++) {
				for (int j = 0; j < row_wordset_test.length; j++) {
					if (unimportant_words[i].equals(row_wordset_test[j])) {
						row_wordset_test[j] = " ";
						deleted++;
                    }
                    
					for (int k = 33; k < 256; k++) {
						if ((k > 64 && k < 91) || (k > 96 && k < 123)) {
							char ascii_char = (char) k;
							String important_words = "" + ascii_char;

							if (important_words.equals(row_wordset_test[j])) {
								row_wordset_test[j] = " ";
								deleted++;
							}

						} else if (k > 47 && k < 58) {
							char ascii_char = (char) k;
							String important_words = "" + ascii_char;

							if (important_words.equals(row_wordset_test[j])) {
								row_wordset_test[j] = " ";
								deleted++;
							}

						} else {
							char ascii_char = (char) k;
							String important_words = "\\" + ascii_char;

							if (important_words.equals(row_wordset_test[j])) {
								row_wordset_test[j] = " ";
								deleted++;
							}

						}
					}
				}
			}

			String[] row_wordset = new String[row_wordset_test.length - deleted];
			int index_wordset = 0;

			for (int i = 0; i < row_wordset_test.length; i++) {
				if (!row_wordset_test[i].equals(" ")) {
					row_wordset[index_wordset] = row_wordset_test[i];
					index_wordset++;
				}
			}

			String first_word = "";
			String second_word = "";
			for (int i = 0; i < row_wordset.length; i++) {
				//one word_collection
				first_word = row_wordset[i];

				word_collection.set(first_word);
				output.collect(word_collection, one);
				reporter.incrCounter(Counters.INPUT_WORDS, 1);
				for (int j = i + 1; j < row_wordset.length; j++) {
					second_word = row_wordset[j];
					//two words
					if (first_word.equals(second_word)) {
						continue;
					}

					String key_word = first_word + " " + second_word;
					String inverse_key = second_word + " " + first_word;
					if (first_word.compareTo(second_word) > 0) {
						key_word = inverse_key;
					}

					word_collection.set(key_word);
					output.collect(word_collection, one);
					reporter.incrCounter(Counters.INPUT_WORDS, 1);
				}
			}

			if ((++repetitions % 100) == 0) {
				reporter.setStatus("Finished processing " + repetitions + " records " + "from the input file: " + input_file);
			}
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output,
				Reporter reporter) throws IOException {

			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}

			boolean write_key = true;

			for (int j = 0; j < most_repeated_words.length; j++) {
				if (most_repeated_words[j].equals(key)) {
					write_key = false;
					break;
				}
			}

			if (write_key) {

				for (int i = most_repeated_values.length - 1; i >= 0; i--) {
					if (sum > most_repeated_values[i] && write_key) {
						if (i < most_repeated_values.length - 1) {
							most_repeated_values[i + 1] = most_repeated_values[i];
							most_repeated_words[i + 1] = most_repeated_words[i];
							most_repeated_values[i] = 0;
							most_repeated_words[i] = "";
						}
						most_repeated_values[i] = sum;
						most_repeated_words[i] = key.toString();
					}
				}

			}

			output.collect(key, new IntWritable(sum));
		}
	}

	public int run(String[] args) throws Exception {
		JobConf conf = new JobConf(getConf(), WordCount.class);
		conf.setJobName("wordcount");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		List<String> other_args = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			if ("-skip".equals(args[i])) {
				DistributedCache.addCacheFile(new Path(args[++i]).toUri(), conf);
				conf.setBoolean("wordcount.skip.patterns", true);
			} else {
				other_args.add(args[i]);
			}
		}

		FileInputFormat.setInputPaths(conf, new Path(other_args.get(0)));
		FileOutputFormat.setOutputPath(conf, new Path(other_args.get(1)));

		JobClient.runJob(conf);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new WordCount(), args);

		System.out.println("The most repeated word is: '" + most_repeated_words[3] + " " + most_repeated_words[4] + "' => " + most_repeated_values[3] + " times.");

		System.exit(res);
	}

	static String[] most_repeated_words = { "", "", "", "", "" };
	static int[] most_repeated_values = { 0, 0, 0, 0, 0 };
}