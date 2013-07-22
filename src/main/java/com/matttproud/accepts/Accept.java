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

import net.jcip.annotations.ThreadSafe;

import com.google.common.collect.ImmutableMap;

/**
 * <p>
 * {@link Accept} models a single HTTP Accept header media-type specification
 * per <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">RFC2616
 * Sec. 14</a>.
 * </p>
 * 
 * <p>
 * {@link Accept} entities are generated by either parsing a request with
 * {@link Parser#parse(javax.servlet.http.HttpServletRequest)} or by extracting
 * information from a header body with {@link Parser#parse(String)}.
 * </p>
 * 
 * <p>
 * Through {@link Comparable}, {@link Accept} is sortable in descending order of
 * {@code q} parameter and specificity.
 * </p>
 * 
 * @author Matt T. Proud (matt.proud@gmail.com)
 * @see Parser
 */
@ThreadSafe
public final class Accept implements Comparable<Accept> {
  private final String type;
  private final String subtype;
  private final float q;
  private final ImmutableMap<String, String> params;

  /**
   * <p>
   * Build an {@link Accept} entity.
   * </p>
   * 
   * <p>
   * All parameter names correspond to their RFC2616 Sec. 14 counterparts.
   * </p>
   */
  public Accept(final String type, final String subtype, final float q,
      final ImmutableMap<String, String> params) {
    this.type = type;
    this.subtype = subtype;
    this.q = q;
    this.params = params;
  }

  /**
   * <p>
   * Emit the overall type parameter for the request.
   * </p>
   * 
   * <p>
   * For {@code Accept: application/json;q=1.0;schema=Object;version=0.0.1},
   * this maps to {@code application}.
   * </p>
   */
  public String getType() {
    return type;
  }

  /**
   * <p>
   * Emit the subtype parameter for the request.
   * </p>
   * 
   * <p>
   * For {@code Accept: application/json;q=1.0;schema=Object;version=0.0.1},
   * this maps to {@code json}.
   * </p>
   */
  public String getSubtype() {
    return subtype;
  }

  /**
   * <p>
   * Emit the q parameter for the request.
   * </p>
   * 
   * <p>
   * For {@code Accept: application/json;q=1.0;schema=Object;version=0.0.1},
   * this maps to {@code 1.0}.
   * </p>
   * 
   */
  public float getQ() {
    return q;
  }

  /**
   * <p>
   * Emit the parameters parameter for the request.
   * </p>
   * 
   * <p>
   * This excludes the q parameter, which is available through
   * {@link Accept#getQ()}.
   * </p>
   * 
   * <p>
   * For {@code Accept: application/json;q=1.0;schema=Object;version=0.0.1},
   * this maps to {@code schema=Object, version=0.0.1} .
   * </p>
   */
  public ImmutableMap<String, String> getParams() {
    return params;
  }

  /**
   * {@inheritDoc}
   */
  public int compareTo(final Accept o) {
    if (q < o.getQ()) {
      return 1;
    }
    if (q > o.getQ()) {
      return -1;
    }
    if (type.equals("*") && !o.getType().equals("*")) {
      return 1;
    }
    if (subtype.equals("*") && !o.getSubtype().equals("*")) {
      return 1;
    }

    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof Accept)) return false;

    final Accept accept = (Accept) o;

    if (Float.compare(accept.q, q) != 0) return false;
    if (!params.equals(accept.params)) return false;
    if (!subtype.equals(accept.subtype)) return false;
    if (!type.equals(accept.type)) return false;

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + subtype.hashCode();
    result = 31 * result + (q != +0.0f ? Float.floatToIntBits(q) : 0);
    result = 31 * result + params.hashCode();
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("Accept{ type: %s, subtype: %s, q: %f, params: %s }", type, subtype, q,
        params);
  }
}
