	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.io.PrintWriter;
	
	
public class ScriptGenerator {

		public static void main(String[] args) throws IOException {
			if(args.length < 4){
				System.out.println("Incorrect args");
				System.exit(0);
			}

			int expectBeg = Integer.parseInt(args[0]);
			int expectEnd = Integer.parseInt(args[1]);
			int actualBeg = Integer.parseInt(args[2]);
			int actualEnd = Integer.parseInt(args[3]);
			
			File executable = new File("BloomFilter.bat");
			if(executable.exists()) executable.delete();
			executable.createNewFile();
			
			PrintWriter writer = new PrintWriter(executable);
			
			for(int i = expectBeg; i <= expectEnd; i+=10){
				for(int j=i; j <=actualEnd; j+=10){
					writer.printf("java BloomFilter %d %d\n", i,j);
				}
			}
			
			writer.close();
		}

	

}
