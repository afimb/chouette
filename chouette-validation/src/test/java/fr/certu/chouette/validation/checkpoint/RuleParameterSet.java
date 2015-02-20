package fr.certu.chouette.validation.checkpoint;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class RuleParameterSet extends JSONObject
{
   public RuleParameterSet() throws JSONException, IOException
   {
      this("full");
   }

   public RuleParameterSet(String filename) throws JSONException, IOException
   {
      super(FileUtils.readFileToString(new File(
            "src/test/data/"+filename+"parameterset.json")));

   }

}
