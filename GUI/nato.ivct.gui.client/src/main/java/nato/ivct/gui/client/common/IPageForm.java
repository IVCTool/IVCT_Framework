/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package nato.ivct.gui.client.common;

import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCloseButton;
import org.eclipse.scout.rt.platform.exception.ProcessingException;

public interface IPageForm extends IForm {

  /**
   * start the PageFormHandler
   */
  void startPageForm() throws ProcessingException;

  AbstractCloseButton getCloseButton() throws ProcessingException;

}

