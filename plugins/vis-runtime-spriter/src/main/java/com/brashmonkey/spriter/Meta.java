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

package com.brashmonkey.spriter;

public class Meta {

	Var[] vars;

	static class Var {
		int id;
		String name;
		Value def;
		Key[] keys;

		static class Key {
			int id;
			long time;
			Value value;

			public Value getValue () {
				return value;
			}

			public Class<?> getType () {
				return this.value.getClass();
			}

			public int getId () {
				return this.id;
			}

			public long getTime () {
				return this.time;
			}
		}

		static class Value {
			Object value;

			public int getInt () {
				return (Integer) value;
			}

			public long getLong () {
				return (Long) value;
			}

			public String getString () {
				return (String) value;
			}
		}

		public Key get (long time) {
			for (Key key : this.keys)
				if (key.time == time)
					return key;
			return null;
		}

		public boolean has (long time) {
			return this.get(time) != null;
		}

		public String getName () {
			return this.name;
		}

		public int getId () {
			return this.id;
		}

		public Value getDefault () {
			return this.def;
		}
	}

	public Var getVar (long time) {
		for (Var var : this.vars)
			if (var.get(time) != null)
				return var;
		return null;
	}
}
