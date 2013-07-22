/*
 * Copyright 2013 Matt T. Proud Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.matttproud.accepts;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * {@link Parser} generates {@link Accept} entities for the provided requests
 * per <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">RFC2616
 * Sec. 14</a>.
 * </p>
 * 
 * <p>
 * Critical notes:
 * <ul>
 * <li>If the provided inputs lack the relevant q parameter, a default value of
 * {@code 1.0} is inferred.</li>
 * <li>Emitted {@link Accept} entities are sorted in descending order per RFC
 * guidelines.</li>
 * </ul>
 * </p>
 * 
 * @author Matt T. Proud (matt.proud@gmail.com)
 * @see Accept
 */
@ThreadSafe
public class Parser {
  private static final String SPEC_DELIMITER = ",";
  private static final float DEFAULT_Q = 1.0f;
  private static final String PARAM_DELIMITER = ";";
  private static final String TYPE_DELIMITER = "/";
  private static final String DEFAULT_SUBTYPE = "";
  private static final String TYPE_WILDCARD = "*";
  private static final String Q_PARAMETER = "q";
  private static final String PARAMETER_ASSIGNMENT = "=";

  /**
   * <p>
   * Generate {@link Accept} entities from a {@link HttpServletRequest}.
   * </p>
   * 
   * @param r The request.
   * @return A group of {@link Accept} entities.
   * @throws IllegalArgumentException if an illegal header value is provided.
   */
  public static ImmutableList<Accept> parse(final HttpServletRequest r)
      throws IllegalArgumentException {
    return parse(Strings.nullToEmpty(r.getHeader("Accept")));
  }

  /**
   * <p>
   * Generate {@link Accept} entities from a textual representation of an Accept
   * header value.
   * </p>
   * 
   * @param headerValue The header value.
   * @return A group of {@link Accept} entities.
   * @throws IllegalArgumentException if an illegal header value is provided.
   */
  public static ImmutableList<Accept> parse(final String headerValue)
      throws IllegalArgumentException {
    try {
      final String[] specs = headerValue.split(SPEC_DELIMITER);
      final List<Accept> accumulator = new ArrayList<Accept>(specs.length);

      for (String spec : specs) {
        spec = spec.trim();
        float q = DEFAULT_Q;
        final String[] specComponents = spec.split(PARAM_DELIMITER);
        final String media = specComponents[0];
        final String[] types = media.split(TYPE_DELIMITER);
        final String type = types[0].trim();

        String subtype = DEFAULT_SUBTYPE;

        switch (types.length) {
          case 1:
            if (type.equals(TYPE_WILDCARD)) {
              subtype = TYPE_WILDCARD;
            }
          case 2:
            subtype = types[1];
          default:
        }

        if (specComponents.length == 1) {
          accumulator.add(new Accept(type, subtype, q, ImmutableMap.<String, String> of()));
          continue;
        }

        final ImmutableMap.Builder<String, String> params = ImmutableMap.builder();
        for (int i = 1; i < specComponents.length; i++) {
          final String[] elements = specComponents[i].split(PARAMETER_ASSIGNMENT);
          if (elements.length != 2) {
            continue;
          }
          final String name = elements[0].trim();
          final String value = elements[1].trim();
          if (name.equals(Q_PARAMETER)) {
            q = Float.parseFloat(value);
            continue;
          }

          params.put(name, value);
        }

        accumulator.add(new Accept(type, subtype, q, params.build()));
      }
      Collections.sort(accumulator);

      return ImmutableList.copyOf(accumulator);
    } catch (final RuntimeException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
