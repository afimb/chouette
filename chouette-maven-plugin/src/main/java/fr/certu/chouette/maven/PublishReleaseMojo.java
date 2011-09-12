package fr.certu.chouette.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal publish-release
 * 
 */
public class PublishReleaseMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${chouette.publishRelease.outputDirectory}" default-value="releases"
     * @required
     */
    private File outputDirectory;
    
    /**
     * Location of the file.
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private File buildDirectory;
    
    /**
     * Project name.
     * @parameter default-value="${project.name}"
     * @required
     */
    private String projectName;
    
    /**
     * Project version.
     * @parameter default-value="${project.version}"
     * @required
     */
    private String projectVersion;
    
    /**
     * Build timestamp.
     * @parameter default-value="${maven.build.timestamp}"
     * @required
     */
    private String buildTimestamp;

    /**
     * Build artifact.
     * @parameter default-value="${artifactId}"
     * @required
     */
    private String buildArtifact;

    /**
     * Build inputName.
     * @parameter default-value="${project.build.finalName}.tar.gz"
     * @required
     */
    private String inputName; 
    
    /**
     * Build outputName.
     * @parameter expression="${chouette.publishRelease.outputName}"
     */
    private String outputName; 
    
    /**
     * Max releases count.
     * @parameter expression="${chouette.publishRelease.releasesCount}" default-value=0
     */
    private int maxReleasesCount;

    /**
     * Activate artifact folder boolean.
     * @parameter expression="${chouette.publishRelease.activateArtifactFolder}" default-value=false
     */
    private boolean activateArtifactFolder;
    
    /**
     * Activate artifact exec boolean.
     * @parameter expression="${chouette.publishRelease.activateExec}" default-value=false
     */
    private boolean activateExec;
    
    public void execute()
        throws MojoExecutionException
    {
        if(!activateExec){
        	return;
        }
    	if(activateArtifactFolder){
        	outputDirectory = new File(outputDirectory+"/"+buildArtifact);
        }
    	File f = outputDirectory;

        if ( !f.exists() )
        {
        	getLog().info("Creating output directory : "+outputDirectory);
            f.mkdirs();
        }

        try {
        	if(outputName != null){
        		getLog().info("Copying file "+buildDirectory+"/"+inputName+" to "+outputDirectory+"/"+outputName);
        		FileUtils.copyFile(new File(buildDirectory+"/"+inputName), new File(outputDirectory+"/"+outputName),false);
        	}
        	else{
        		getLog().info("Copying file "+buildDirectory+"/"+inputName+" to "+outputDirectory);
        		FileUtils.copyFileToDirectory(new File(buildDirectory+"/"+inputName), outputDirectory,false);
        	}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
        
        File touch = new File( f, "latest.txt" );
        getLog().info("Creating latest.txt file");
        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( buildLatest() );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
        
        if(maxReleasesCount > 0){
        	keepMostRecentFiles(maxReleasesCount);
        }
    }
    
    private String buildLatest(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("Project: ").append(projectName);
    	sb.append("\nVersion: ").append(projectVersion);
    	sb.append("\nDate: ").append(buildTimestamp);
    	sb.append("\nArtifact: ").append(buildArtifact);
    	sb.append("\nOutputFile: ").append(outputName);
    	return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
	private void keepMostRecentFiles(int numberOfFiles){
    	getLog().info("Keep the "+numberOfFiles+" most recent files");
    	List<File> dirFiles = new ArrayList<File>(FileUtils.listFiles(outputDirectory, new String[]{"tar.gz"}, false));
    	if(dirFiles.size() > numberOfFiles){
	    	Collections.sort(dirFiles, new Comparator<File>() {
	
				public int compare(File f1, File f2) {
					return (int) (f2.lastModified()-f1.lastModified());
				}
			});
	    	dirFiles = dirFiles.subList(numberOfFiles, dirFiles.size());
	    	for(File fileToDelete : dirFiles){
	    		getLog().info("Deleting old file : "+fileToDelete.getName());
	    		fileToDelete.delete();
	    	}
    	}
    }
}
