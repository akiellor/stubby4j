/*
A Java-based HTTP stub server

Copyright (C) 2012 Alexander Zagniotov, Isa Goksu and Eric Mrak

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.stubby;

import org.apache.commons.cli.ParseException;
import org.stubby.cli.ANSITerminal;
import org.stubby.cli.CommandLineIntepreter;
import org.stubby.server.JettyManager;
import org.stubby.server.JettyManagerFactory;

import java.util.Map;

final class Main {

   private Main() {

   }

   public static void main(final String[] args) {

      parseCommandLineArgs(args);
      if (printHelpIfRequested())
         return;

      verifyYamlDataProvided();
      startStubby4jUsingCommandLineArgs();
   }

   private static void parseCommandLineArgs(final String[] args) {
      try {
         CommandLineIntepreter.parseCommandLine(args);
      } catch (final ParseException ex) {
         final String msg = String.format("Could not parse provided command line arguments, error: %s", ex.toString());
         System.err.println(msg);
         throw new RuntimeException(msg);
      }
   }

   private static boolean printHelpIfRequested() {
      if (CommandLineIntepreter.isHelp()) {
         CommandLineIntepreter.printHelp(Main.class);

         return true;
      }
      return false;
   }

   private static void verifyYamlDataProvided() {
      if (!CommandLineIntepreter.isYamlProvided()) {
         final String msg = String.format("YAML data was not provided using command line option '--%s'. %sTo see all command line options run again with option '--%s'",
               CommandLineIntepreter.OPTION_CONFIG,
               "\n",
               CommandLineIntepreter.OPTION_HELP);
         System.err.println(msg);
         throw new RuntimeException(msg);
      }
   }

   private static void startStubby4jUsingCommandLineArgs() {
      try {
         final Map<String, String> commandLineArgs = CommandLineIntepreter.getCommandlineParams();
         final String yamlConfigFilename = commandLineArgs.get(CommandLineIntepreter.OPTION_CONFIG);

         ANSITerminal.muteConsole(CommandLineIntepreter.isMute());

         final JettyManager jettyManager = new JettyManagerFactory().construct(yamlConfigFilename, commandLineArgs);
         jettyManager.startJetty();

      } catch (final Exception ex) {
         final String msg = String.format("Could not init stubby4j, error: %s", ex.toString());
         System.err.println(msg);
         throw new RuntimeException(msg);
      }
   }
}