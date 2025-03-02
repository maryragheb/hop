/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.workflow.actions.delay;

import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.dialog.MessageBox;
import org.apache.hop.ui.core.widget.LabelTextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.workflow.action.ActionDialog;
import org.apache.hop.ui.workflow.dialog.WorkflowDialog;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** This dialog allows you to edit the delay action settings. */
public class ActionDelayDialog extends ActionDialog {
  private static final Class<?> PKG = ActionDelay.class;

  private Text wName;

  private CCombo wScaleTime;

  private LabelTextVar wMaximumTimeout;

  private ActionDelay action;

  private boolean changed;

  public ActionDelayDialog(
      Shell parent, ActionDelay action, WorkflowMeta workflowMeta, IVariables variables) {
    super(parent, workflowMeta, variables);
    this.action = action;
    if (this.action.getName() == null) {
      this.action.setName(BaseMessages.getString(PKG, "ActionDelay.Title"));
    }
  }

  @Override
  public IAction open() {
    Shell parent = getParent();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.MAX | SWT.RESIZE);
    shell.setMinimumSize(400, 190);
    PropsUi.setLook(shell);
    WorkflowDialog.setShellImage(shell, action);

    ModifyListener lsMod = e -> action.setChanged();
    changed = action.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = PropsUi.getFormMargin();
    formLayout.marginHeight = PropsUi.getFormMargin();

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "ActionDelay.Title"));

    int middle = props.getMiddlePct();
    int margin = PropsUi.getMargin();

    // Name line
    Label wlName = new Label(shell, SWT.RIGHT);
    wlName.setText(BaseMessages.getString(PKG, "System.ActionName.Label"));
    wlName.setToolTipText(BaseMessages.getString(PKG, "System.ActionName.Tooltip"));
    PropsUi.setLook(wlName);
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment(0, 0);
    fdlName.right = new FormAttachment(middle, -margin);
    fdlName.top = new FormAttachment(0, margin);
    wlName.setLayoutData(fdlName);
    wName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    PropsUi.setLook(wName);
    wName.addModifyListener(lsMod);
    FormData fdName = new FormData();
    fdName.left = new FormAttachment(middle, 0);
    fdName.top = new FormAttachment(0, margin);
    fdName.right = new FormAttachment(100, 0);
    wName.setLayoutData(fdName);

    // MaximumTimeout line
    wMaximumTimeout =
        new LabelTextVar(
            variables,
            shell,
            BaseMessages.getString(PKG, "ActionDelay.MaximumTimeout.Label"),
            BaseMessages.getString(PKG, "ActionDelay.MaximumTimeout.Tooltip"));
    PropsUi.setLook(wMaximumTimeout);
    wMaximumTimeout.addModifyListener(lsMod);
    FormData fdMaximumTimeout = new FormData();
    fdMaximumTimeout.left = new FormAttachment(0, 0);
    fdMaximumTimeout.top = new FormAttachment(wName, margin);
    fdMaximumTimeout.right = new FormAttachment(100, 0);
    wMaximumTimeout.setLayoutData(fdMaximumTimeout);

    // Whenever something changes, set the tooltip to the expanded version:
    wMaximumTimeout.addModifyListener(
        e -> wMaximumTimeout.setToolTipText(variables.resolve(wMaximumTimeout.getText())));

    // Scale time

    wScaleTime = new CCombo(shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
    wScaleTime.add(BaseMessages.getString(PKG, "ActionDelay.SScaleTime.Label"));
    wScaleTime.add(BaseMessages.getString(PKG, "ActionDelay.MnScaleTime.Label"));
    wScaleTime.add(BaseMessages.getString(PKG, "ActionDelay.HrScaleTime.Label"));
    wScaleTime.select(0); // +1: starts at -1

    PropsUi.setLook(wScaleTime);
    FormData fdScaleTime = new FormData();
    fdScaleTime.left = new FormAttachment(middle, 0);
    fdScaleTime.top = new FormAttachment(wMaximumTimeout, margin);
    fdScaleTime.right = new FormAttachment(100, 0);
    wScaleTime.setLayoutData(fdScaleTime);

    Button wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    wOk.addListener(SWT.Selection, e -> ok());
    Button wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
    wCancel.addListener(SWT.Selection, e -> cancel());

    BaseTransformDialog.positionBottomButtons(shell, new Button[] {wOk, wCancel}, margin, null);

    getData();

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return action;
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {
    if (action.getName() != null) {
      wName.setText(action.getName());
    }
    if (action.getMaximumTimeout() != null) {
      wMaximumTimeout.setText(action.getMaximumTimeout());
    }

    wScaleTime.select(action.scaleTime);

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    action.setChanged(changed);
    action = null;
    dispose();
  }

  private void ok() {
    if (Utils.isEmpty(wName.getText())) {
      MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
      mb.setText(BaseMessages.getString(PKG, "System.TransformActionNameMissing.Title"));
      mb.setMessage(BaseMessages.getString(PKG, "System.ActionNameMissing.Msg"));
      mb.open();
      return;
    }
    action.setName(wName.getText());
    action.setMaximumTimeout(wMaximumTimeout.getText());
    action.scaleTime = wScaleTime.getSelectionIndex();
    dispose();
  }
}
