/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc_lib;

public class LineUtil {

	public static String replaceMacro(String inString)
	{
		StringBuilder env = new StringBuilder();
		StringBuilder out = new StringBuilder();
		int len;

		env.setLength(512);
		out.setLength(512);
		out.setLength(0);
		len = inString.length();

		// Loop through inString character by character
		for (int i = 0, l = 0; i < len; i++, l++)
		{
			boolean gotClosing = false;
			boolean gotEnv = false;
			if (i < len - 1) {
				// Check if we have "$(" in inString
				if (inString.charAt(i) == '$' && inString.charAt(i + 1) == '(')
				{
					gotClosing = false;
					for (int j = i + 2, k = 0; j < len; j++, k++)
					{
						if (inString.charAt(j) == ')')
						{
							String b = null;
							gotClosing = true;
							gotEnv = true;
							env.setLength(k);
							if (env.length() > 0) {
								b = System.getenv(env.toString());
							} else {
								System.out.println("Missing environment variable ");
							}
							if (b != null)
							{
								out.append(b);
								l += b.length() - 1;
								i = j;
							}
							else
							{
								System.out.println("Environment variable not found: " + env.toString());
								String s = inString.subSequence(i, i + k + 3).toString();
								out.append(s);
								l += j - i;
								i = j;
							}
							break;
						}
						env.setCharAt(k, inString.charAt(j));
					}
					if (gotClosing == false)
					{
						System.out.println("Missing closing bracket");
					}
				}
			}
			if (gotEnv)
			{
				gotEnv = false;
				continue;
			}
			out.insert(l, inString.charAt(i));
			out.setLength(l + 1);
		}

		return out.toString();
	}
}