import java.nio.file.Path;

public class main {
    public static void main(String[] args) {
        if(args.length != 3) {
            System.err.println("Usage: gradle run {seirsFromPrepared,seirsFromExternal,prepare} config script");
            System.exit(1);
        }
        if(System.getenv("REGTOKEN") == null) {
            System.err.println("You need to set the environment variable REGTOKEN to the authorization token for the local registry");
            System.exit(2);
        }
        if(args[0].equals("prepare")) {
            PrepareParams pp = new PrepareParams();
            pp.run(Path.of(args[1]), Path.of(args[2]), System.getenv("REGTOKEN"));
        }else if(args[0].equals("seirsFromPrepared")) {
            SEIRS s = new SEIRS();
            s.runFromPrepared(Path.of(args[1]), Path.of(args[2]), System.getenv("REGTOKEN"));
        }else if(args[0].equals("seirsFromExternal")) {
            SEIRS s = new SEIRS();
            s.runFromExternal(Path.of(args[1]), Path.of(args[2]), System.getenv("REGTOKEN"));
        }else{
            System.err.println("first argument must be 'seirsFromExternal', 'seirsFromPrepared' or 'seirs'");
        }
    }

}
