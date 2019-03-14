package ch.heigvd.res.labio.impl.explorers;

import ch.heigvd.res.labio.interfaces.IFileExplorer;
import ch.heigvd.res.labio.interfaces.IFileVisitor;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This implementation of the IFileExplorer interface performs a depth-first
 * exploration of the file system and invokes the visitor for every encountered
 * node (file and directory). When the explorer reaches a directory, it visits all
 * files in the directory and then moves into the subdirectories.
 * 
 * @author Olivier Liechti
 */
public class DFSFileExplorer implements IFileExplorer {

  @Override
  public void explore(File rootDirectory, IFileVisitor visitor) {

    visitor.visit(rootDirectory);

    ArrayList<File> fileOnlyList = new ArrayList<>();

    if(rootDirectory.isDirectory()){

      File[] fileList =rootDirectory.listFiles();

      Arrays.sort(fileList);

      //Visit all subdirectories
      for(File file : fileList){
        if(file.isDirectory())
          explore(file, visitor);
        else
          fileOnlyList.add(file);
      }

      // Explore each file
      for(File f : fileOnlyList){
        explore(f, visitor);
      }
    }
  }
}
