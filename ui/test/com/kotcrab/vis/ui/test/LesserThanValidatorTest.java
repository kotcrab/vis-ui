/*
 * Copyright 2014-2016 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.ui.test;

import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LesserThanValidatorTest {
	@Test
	public void testValidateInput () throws Exception {
		LesserThanValidator validator = new LesserThanValidator(5);
		LesserThanValidator validatorEquals = new LesserThanValidator(5, true);

		assertFalse(validator.validateInput("A"));
		assertFalse(validator.validateInput(""));
		assertFalse(validatorEquals.validateInput("A"));
		assertFalse(validatorEquals.validateInput(""));

		assertFalse(validator.validateInput("5"));
		assertTrue(validator.validateInput("4"));
		assertTrue(validatorEquals.validateInput("5"));
		assertTrue(validatorEquals.validateInput("4"));

		assertFalse(validator.validateInput(String.valueOf(Float.MAX_VALUE)));
		assertFalse(validatorEquals.validateInput(String.valueOf(Float.MAX_VALUE)));

		assertTrue(validator.validateInput(String.valueOf(Float.MIN_VALUE)));
		assertTrue(validatorEquals.validateInput(String.valueOf(Float.MIN_VALUE)));
	}
}
