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
 * Defines a directive for creating Loader
 * @author Oleksii Kurinnyi
 */
export class CheLoaderCrane {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor() {
    this.restrict = 'E';
    this.replace = true;
    this.templateUrl = 'components/widget/loader/che-crane.html';

    // we require ngModel as we want to use it inside our directive
    //this.require = ['ngModel'];

    // scope values
    this.scope = {
      step: '@cheStep',
      stepsToShow: '@cheSteps',
      switchOnIteration: '@cheSwitchOnIteration'
    };
  }

  compile(element, attr) {

  }

  link ($scope, element) {
    let cargoEl = element.find('#load'),
      oldSteps = [],
      newStep,
      animationRunning = false;
    console.log('steps to show: ', $scope.stepsToShow);

    $scope.$watch(() => {return $scope.step;}, (newVal,oldVal) => {
      if (oldSteps.indexOf(oldVal) === -1) {
        oldSteps.push(oldVal);
      }
      newStep = newVal;

      // animation initialization
      if (animationRunning === false || $scope.switchOnIteration === true){
        animationRunning = true;
        setCurrentStep();
      }
    });

    if ($scope.switchOnIteration === true) {
      // event fires on animation iteration end
      element.find('.anim.trolley-block').bind('animationiteration', () => {
        setCurrentStep();
      });
    }

    let setCurrentStep = () => {
        for (let i=0; i<oldSteps.length; i++) {
          element.removeClass('step-'+oldSteps[i]);
          cargoEl.removeClass('layer-'+oldSteps[i]);
        }
        oldSteps.length = 0;

        element.addClass('step-'+newStep);
        cargoEl.addClass('layer-'+newStep);
    }
  }

}
