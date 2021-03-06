/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards.subflow;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FlowOneStep extends AbstractUICommand implements UIWizardStep
{

   @Inject
   @WithAttributes(label = "Flow One", required = true)
   private UIInput<String> flowOneInput;

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return Results.navigateTo(FlowOneOneStep.class);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(flowOneInput);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success();
   }

}
