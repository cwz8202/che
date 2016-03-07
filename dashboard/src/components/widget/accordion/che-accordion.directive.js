/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

/**
 * Defines a directive for Accordion component
 * @author Oleksii Kurinnyi
 */
export class CheAccordion {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($timeout) {
    this.$timeout = $timeout;
    this.restrict = 'E';
    this.transclude = true;
    this.replace = true;
    this.template = '<div ng-transclude class="che-accordion closed untouched"></div>';

    //this.require = ['ngModel'];

    // scope values
    this.scope = {
      index: '@cheIndex',
      step: '@cheCurrentStep'
    };
  }

  link ($scope, element) {
    let currentBodyElement = element.find('.accordion-body');

    // automatic switching panes
    $scope.$watch(() => {return $scope.step;}, (newVal) => {
      if (element.hasClass('untouched') && $scope.index === newVal) {
        openPane();
      }
    });

    // manual switching panes
    element.bind('click', (event) => {
      if (angular.element(event.target).parent().hasClass('accordion-title')){
        element.removeClass('untouched');
        openPane(element);
      }
    });

    let openPane = () => {
      if (element.hasClass('closed')) {
        let siblingElements = element.siblings();

        // find opened pane and close it
        for (let i=0; i<siblingElements.length; i++){
          let siblingEl = angular.element(siblingElements[i]);
          if (siblingEl.hasClass('closed')){
            continue;
          }

          let siblingBodyEl = siblingEl.find('.accordion-body'),
            siblingBodyHeight = siblingBodyEl[0].scrollHeight;
          siblingBodyEl.css('height', siblingBodyHeight);
          siblingEl.addClass('closed');
          siblingBodyEl.removeAttr('style');
        }

        // open current pane
        let currentBodyHeight = currentBodyElement[0].scrollHeight;
        currentBodyElement.css('height', currentBodyHeight);
        element.removeClass('closed');
        this.$timeout(() => {
          currentBodyElement.removeAttr('style');
        }, 2000);
      }
    }
  }
}
