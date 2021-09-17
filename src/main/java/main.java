import java.nio.file.Path;

public class main {
    public static void main(String[] args) {
        if(args.length != 3) {
            System.err.println("Usage: gradle run {seirs,prepare} config script");
            System.exit(1);
        }
        if(System.getenv("REGTOKEN") == null) {
            System.err.println("You need to set the environment variable REGTOKEN to the authorization token for the local registry");
            System.exit(2);
        }
        if(args[0].equals("prepare")) {
            PrepareParams pp = new PrepareParams();
            pp.run(Path.of(args[1]), Path.of(args[2]), System.getenv("REGTOKEN"));
        }else if(args[0].equals("seirs")) {
            SEIRS s = new SEIRS();
            s.run(Path.of(args[1]), Path.of(args[2]), System.getenv("REGTOKEN"));
        }else{
            System.err.println("first argument must be 'prepare' or 'seirs'");
        }
    }

}
