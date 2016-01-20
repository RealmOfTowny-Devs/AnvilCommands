/**
 * This file is part of AnvilGUI, licensed under the MIT License (MIT).
 *
 * Copyright (c) Cybermaxke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.drkmatr1984.anvilstringcommand.util;

import java.lang.reflect.Field;

/**
 * Some useful reflection utilities.
 */
public class ReflectionUtils {

	/**
	 * Searches for a field with the specified type in the target class at the specific index.
	 * 
	 * @param target the target 
	 * @param fieldType the type of the field
	 * @param index the specific index
	 * @return the field if found, otherwise null
	 */
	public static Field findField(Class<?> target, Class<?> fieldType, int index) {
		for (Field field : target.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getType().isAssignableFrom(fieldType)) {
				if (index == 0) {
					return field;
				}
				index--;
			}
		}
		return null;
	}

}
