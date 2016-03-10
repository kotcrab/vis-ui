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

import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GreaterThanValidatorTest {

	@Test
	public void testValidateInput () throws Exception {
		GreaterThanValidator validator = new GreaterThanValidator(5);
		GreaterThanValidator validatorEquals = new GreaterThanValidator(5, true);

		assertFalse(validator.validateInput("A"));
		assertFalse(validator.validateInput(""));
		assertFalse(validatorEquals.validateInput("A"));
		assertFalse(validatorEquals.validateInput(""));

		assertFalse(validator.validateInput("5"));
		assertTrue(validator.validateInput("6"));
		assertTrue(validatorEquals.validateInput("5"));
		assertTrue(validatorEquals.validateInput("6"));

		assertTrue(validator.validateInput(String.valueOf(Float.MAX_VALUE)));
		assertTrue(validatorEquals.validateInput(String.valueOf(Float.MAX_VALUE)));

		assertFalse(validator.validateInput(String.valueOf(Float.MIN_VALUE)));
		assertFalse(validatorEquals.validateInput(String.valueOf(Float.MIN_VALUE)));
	}
}
