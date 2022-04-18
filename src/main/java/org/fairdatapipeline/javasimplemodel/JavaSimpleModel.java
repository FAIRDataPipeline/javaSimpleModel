package org.fairdatapipeline.javasimplemodel;

import java.io.File;
import java.nio.file.Path;

public class JavaSimpleModel {
  private static final String ENV_VAR_NAME = "FDP_LOCAL_TOKEN";

  public static void main(String[] args) {
    if (System.getenv(ENV_VAR_NAME) == null) {
      System.err.println(
          "The fair CLI needs to set the environment variable FDP_LOCAL_TOKEN to the authorization token for the local registry");
      System.exit(2);
    }
    String confDir = "";
    if (args.length == 1) {
      confDir = args[0];
    } else if (args.length == 2) {
      confDir = args[1];
    } else {
      System.err.println(
          "Usage: gradle run --args=\"[{--seirsFromPrepared,--prepare}] configDir\"");
      System.exit(1);
    }
    if (!new File(confDir).isDirectory()) {
      System.err.println("Config dir " + confDir + " doesn't seem to exist.");
      System.exit(1);
    }
    Path configFile = Path.of(confDir).resolve("config.yaml");
    Path scriptFile = Path.of(confDir).resolve("script.sh");
    if (args.length == 1) {
      SEIRS s = new SEIRS();
      s.runFromExternal(configFile, scriptFile, System.getenv(ENV_VAR_NAME));
    } else if (args[0].equals("--prepare")) {
      PrepareParams pp = new PrepareParams();
      pp.run(configFile, scriptFile, System.getenv(ENV_VAR_NAME));
    } else if (args[0].equals("--seirsFromPrepared")) {
      SEIRS s = new SEIRS();
      s.runFromPrepared(configFile, scriptFile, System.getenv(ENV_VAR_NAME));
    } else {
      System.err.println(
          "Usage: gradle run --args=\"[{--seirsFromPrepared,--prepare}] configDir\"");
      System.exit(1);
    }
  }
}
