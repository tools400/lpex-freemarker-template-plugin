package com.freemarker.lpex;

import com.freemarker.lpex.formdialogs.LPEXTemplate;
import com.freemarker.lpex.utils.PluginLogger;
import com.freemarker.lpex.utils.StackTraceUtil;
import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

import freemarker.template.TemplateException;

import java.io.*;

public class Actions {

	public static class insertTemplate implements LpexAction {
		
		public void doAction(LpexView view) {
			try {
				//TODO Make this path configurable
				String baseTemplateFolder = "C:/Documents and Settings/RNewton/IBM/rationalsdp/workspace/com.freemarker.lpex/examples";
				
				LPEXManipulator lpexManipulator = new LPEXManipulator(view);
				final String templateHint = lpexManipulator.getCursorWord();
				String parser = lpexManipulator.getParser();
				
				File templateDirectory = new File(baseTemplateFolder + "/" + parser);
				FilenameFilter templateFilter = new FilenameFilter() { 
					public boolean accept(File dir, String name) {
						File file = new File(dir + "/" + name);
						if (file.isFile()) {
					        String extension = file.toString().substring(file.toString().lastIndexOf("."));
							if (extension.compareToIgnoreCase(".ftl") == 0) {
								return name.toUpperCase().startsWith(templateHint.toUpperCase());
							}
						}
						return false;
					}
				};
				
				//PluginLogger.logger.info("Present popup list of templates");
				
				String[] templateFiles = templateDirectory.list(templateFilter);
				String selectedTemplate = "";
				lpexManipulator.promptTemplateChooser(templateFiles);
				selectedTemplate = lpexManipulator.getSelectedTemplateNameNoExt();
				if (selectedTemplate == "") { 
					// no matches found
					return;
				} 
				
				//PluginLogger.logger.info("Selected: " + selectedTemplate);
				
				File templateFile = new File(baseTemplateFolder + "/" + parser + "/" + selectedTemplate + ".ftl");
				
				LPEXTemplate lpexTemplate = new LPEXTemplate(templateFile);
				
				//Parse the form configuration that will dictate the form dialog structure
				lpexTemplate.buildForm();
				
				//Get the form data container ready to receive data
				lpexTemplate.initializeFormData();
				
				//Present the dialogs for the user to fill out
				lpexTemplate.getForm().open();
				
				//PluginLogger.logger.info(lpexTemplate.getFormDataAsString());
				//PluginLogger.logger.info(lpexTemplate.formData.toString());
				
				//Merge the collected data with the template
				lpexTemplate.merge();
				
				//Insert the merged template into the cursor position of the current LPEX document
				lpexManipulator.addBlockTextAtCursorPosition(lpexTemplate.getResult());
				
			} catch (TemplateException e) {
				PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
			} catch (IOException e) {
				PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
			} catch (Exception e) {
				PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
			}

			return;
		}

		public boolean available(LpexView view)
		{
			return true;
			//return (view.query("block.type").equalsIgnoreCase("element") && LPEXManipulator.isInBlock(view)) ;
		}
	}
}