/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.client.settings.compiler;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.ide.api.preferences.AbstractPreferencePagePresenter;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.inject.factories.PropertyWidgetFactory;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidget;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.COMPARING_IDENTICAL_VALUES;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.COMPILER_UNUSED_IMPORT;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.COMPILER_UNUSED_LOCAL;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.DEAD_CODE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.FIELD_HIDES_ANOTHER_VARIABLE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.METHOD_WITH_CONSTRUCTOR_NAME;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.MISSING_DEFAULT_CASE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.MISSING_OVERRIDE_ANNOTATION;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.MISSING_SERIAL_VERSION_UID;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.NO_EFFECT_ASSIGNMENT;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.NULL_POINTER_ACCESS;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.POTENTIAL_NULL_POINTER_ACCESS;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.REDUNDANT_NULL_CHECK;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.TYPE_PARAMETER_HIDE_ANOTHER_TYPE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.UNCHECKED_TYPE_OPERATION;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.UNNECESSARY_ELSE_STATEMENT;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.UNUSED_PRIVATE_MEMBER;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.USAGE_OF_RAW_TYPE;

/**
 * The class contains business logic which allow control changing of compiler's properties.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class ErrorWarningsPresenter extends AbstractPreferencePagePresenter implements PropertyWidget.ActionDelegate {

    private final ErrorWarningsView     view;
    private final PropertyWidgetFactory propertyFactory;
    private final PreferencesManager    preferencesManager;

    private final Map<String, PropertyWidget> widgets;

    @Inject
    public ErrorWarningsPresenter(JavaLocalizationConstant locale,
                                  ErrorWarningsView view,
                                  PropertyWidgetFactory propertyFactory,
                                  @JavaCompilerPreferenceManager PreferencesManager preferencesManager) {
        super(locale.compilerSetup());

        this.view = view;

        this.propertyFactory = propertyFactory;
        this.preferencesManager = preferencesManager;

        this.widgets = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDirty() {
        for (Map.Entry<String, PropertyWidget> entry : widgets.entrySet()) {
            String propertyName = entry.getKey();
            String changedValue = entry.getValue().getSelectedValue();
            if (!changedValue.equals(preferencesManager.getValue(propertyName))) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void storeChanges() {
        for (Map.Entry<String, PropertyWidget> entry : widgets.entrySet()) {
            preferencesManager.setValue(entry.getKey(), entry.getValue().getSelectedValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void revertChanges() {
        for (Map.Entry<String, PropertyWidget> entry : widgets.entrySet()) {
            String propertyId = entry.getKey();
            PropertyWidget widget = entry.getValue();

            String previousValue = preferencesManager.getValue(propertyId);

            widget.selectPropertyValue(previousValue);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onPropertyChanged(@NotNull String propertyId, @NotNull String value) {
        delegate.onDirtyChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        addErrorWarningsPanel();

        container.setWidget(view);
    }

    private void addErrorWarningsPanel() {
        preferencesManager.loadPreferences().then(new Operation<Map<String, String>>() {
            @Override
            public void apply(Map<String, String> properties) throws OperationException {
                createAndAddWidget(COMPILER_UNUSED_LOCAL);

                createAndAddWidget(COMPILER_UNUSED_IMPORT);

                createAndAddWidget(DEAD_CODE);

                createAndAddWidget(METHOD_WITH_CONSTRUCTOR_NAME);

                createAndAddWidget(UNNECESSARY_ELSE_STATEMENT);

                createAndAddWidget(COMPARING_IDENTICAL_VALUES);

                createAndAddWidget(NO_EFFECT_ASSIGNMENT);

                createAndAddWidget(MISSING_SERIAL_VERSION_UID);

                createAndAddWidget(TYPE_PARAMETER_HIDE_ANOTHER_TYPE);

                createAndAddWidget(FIELD_HIDES_ANOTHER_VARIABLE);

                createAndAddWidget(MISSING_DEFAULT_CASE);

                createAndAddWidget(UNUSED_PRIVATE_MEMBER);

                createAndAddWidget(UNCHECKED_TYPE_OPERATION);

                createAndAddWidget(USAGE_OF_RAW_TYPE);

                createAndAddWidget(MISSING_OVERRIDE_ANNOTATION);

                createAndAddWidget(NULL_POINTER_ACCESS);

                createAndAddWidget(POTENTIAL_NULL_POINTER_ACCESS);

                createAndAddWidget(REDUNDANT_NULL_CHECK);
            }
        });
    }

    private void createAndAddWidget(@NotNull ErrorWarningsOptions option) {
        String parameterId = option.toString();

        if (widgets.containsKey(parameterId)) {
            return;
        }

        PropertyWidget widget = propertyFactory.create(option);

        String value = preferencesManager.getValue(parameterId);

        widget.selectPropertyValue(value);

        widget.setDelegate(ErrorWarningsPresenter.this);

        widgets.put(parameterId, widget);

        view.addProperty(widget);
    }
}
