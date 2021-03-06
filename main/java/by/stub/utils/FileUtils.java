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

package by.stub.utils;

import by.stub.cli.CommandLineInterpreter;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Zagniotov
 * @since 11/8/12, 8:30 AM
 */
@SuppressWarnings("serial")
public final class FileUtils {

   public static final Set<String> ASCII_TYPES = Collections.unmodifiableSet(
      new HashSet<String>(
         Arrays.asList(
            ".ajx", ".am", ".asa", ".asc", ".asp", ".aspx", ".awk", ".bat",
            ".c", ".cdf", ".cf", ".cfg", ".cfm", ".cgi", ".cnf", ".conf", ".cpp",
            ".css", ".csv", ".ctl", ".dat", ".dhtml", ".diz", ".file", ".forward",
            ".grp", ".h", ".hpp", ".hqx", ".hta", ".htaccess", ".htc", ".htm", ".html",
            ".htpasswd", ".htt", ".htx", ".in", ".inc", ".info", ".ini", ".ink", ".java",
            ".js", ".json", ".jsp", ".log", ".logfile", ".m3u", ".m4", ".m4a", ".mak",
            ".map", ".model", ".msg", ".nfo", ".nsi", ".info", ".old", ".pas", ".patch",
            ".perl", ".php", ".php2", ".php3", ".php4", ".php5", ".php6", ".phtml", ".pix",
            ".pl", ".pm", ".po", ".pwd", ".py", ".qmail", ".rb", ".rbl", ".rbw", ".readme",
            ".reg", ".rss", ".rtf", ".ruby", ".session", ".setup", ".sh", ".shtm", ".shtml",
            ".sql", ".ssh", ".stm", ".style", ".svg", ".tcl", ".text", ".threads", ".tmpl",
            ".tpl", ".txt", ".ubb", ".vbs", ".xhtml", ".xml", ".xrc", ".xsl", ".yaml", ".yml"
         )
      )
   );

   public static final String LINE_SEPARATOR_UNIX = "\n";
   public static final String LINE_SEPARATOR_MAC_OS_PRE_X = "\r";
   public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
   public static final String LINE_SEPARATOR_TOKEN = "[_T_O_K_E_N_]";
   public static final String LINE_SEPARATOR;

   static {
      final int initialSize = 4;
      final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter(initialSize);
      final PrintWriter out = new PrintWriter(stringBuilderWriter);
      out.println();
      LINE_SEPARATOR = stringBuilderWriter.toString();
      out.close();
   }

   private FileUtils() {

   }


   public static byte[] binaryFileToBytes(final String filePath) throws IOException {
      final File contentFile = new File(getDataDirectory(), filePath);

      if (!contentFile.isFile()) {
         throw new IOException(String.format("Could not load file from path: %s", filePath));
      }

      return IOUtils.toByteArray(new FileInputStream(contentFile));
   }

   public static String asciiFileToString(final String filePath) throws IOException {
      final File contentFile = new File(getDataDirectory(), filePath);

      if (!contentFile.isFile()) {
         throw new IOException(String.format("Could not load file from path: %s", filePath));
      }

      final String loadedContent = StringUtils.inputStreamToString(new FileInputStream(contentFile));

      return FileUtils.enforceSystemLineSeparator(loadedContent);
   }

   public static byte[] asciiFileToUtf8Bytes(final String filePath) throws IOException {
      final String loadedContent = FileUtils.asciiFileToString(filePath);

      return loadedContent.getBytes(StringUtils.utf8Charset());
   }


   private static String getDataDirectory() {
      final String yamlConfigFilename = CommandLineInterpreter.getCommandlineParams().get("data");
      if (yamlConfigFilename == null) {
         return CommandLineInterpreter.getCurrentJarLocation(CommandLineInterpreter.class);
      }

      return new File(yamlConfigFilename).getParent();
   }

   public static String enforceSystemLineSeparator(final String loadedContent) {
      if (!StringUtils.isSet(loadedContent)) {
         return "";
      }

      return loadedContent
         .replace(LINE_SEPARATOR_WINDOWS, LINE_SEPARATOR_TOKEN)
         .replace(LINE_SEPARATOR_MAC_OS_PRE_X, LINE_SEPARATOR_TOKEN)
         .replace(LINE_SEPARATOR_UNIX, LINE_SEPARATOR_TOKEN)
         .replace(LINE_SEPARATOR_TOKEN, LINE_SEPARATOR);
   }

   private static final class StringBuilderWriter extends Writer implements Serializable {

      private final StringBuilder builder;

      private StringBuilderWriter(int capacity) {
         this.builder = new StringBuilder(capacity);
      }

      @Override
      public void write(char[] value, int offset, int length) {
         if (value != null) {
            builder.append(value, offset, length);
         }
      }

      @Override
      public void flush() throws IOException {
      }

      @Override
      public void close() throws IOException {
      }

      @Override
      public String toString() {
         return builder.toString();
      }
   }
}