/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.search;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.sonar.server.user.MockUserSession;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

public class QueryContextTest {

  QueryContext options;

  @Before
  public void setUp() throws Exception {
    MockUserSession.set().setLogin("john").setUserGroups("sonar-users");
    options = new QueryContext();
  }

  @Test
  public void user_and_groups() throws Exception {
    assertThat(options.getUserLogin()).isEqualTo("john");
    assertThat(options.getUserGroups()).containsOnly("sonar-users", "Anyone");
  }

  @Test
  public void page_shortcut_for_limit_and_offset() throws Exception {
    options.setPage(3, 10);

    assertThat(options.getLimit()).isEqualTo(10);
    assertThat(options.getOffset()).isEqualTo(20);
  }

  @Test
  public void page_starts_at_one() throws Exception {
    options.setPage(1, 10);
    assertThat(options.getLimit()).isEqualTo(10);
    assertThat(options.getOffset()).isEqualTo(0);
    assertThat(options.getPage()).isEqualTo(1);
  }

  @Test
  public void with_zero_page_size() throws Exception {
    options.setPage(1, 0);
    assertThat(options.getLimit()).isEqualTo(0);
    assertThat(options.getOffset()).isEqualTo(0);
    assertThat(options.getPage()).isEqualTo(0);
  }

  @Test
  public void page_must_be_strictly_positive() throws Exception {
    try {
      options.setPage(0, 10);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Page must be greater or equal to 1 (got 0)");
    }
  }

  @Test
  public void page_size_must_be_positive() throws Exception {
    try {
      options.setPage(2, -1);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Page size must be greater or equal to 0 (got -1)");
    }
  }

  @Test
  public void max_limit() throws Exception {
    options.setLimit(42);
    assertThat(options.getLimit()).isEqualTo(42);

    options.setLimit(QueryContext.MAX_LIMIT + 10);
    assertThat(options.getLimit()).isEqualTo(QueryContext.MAX_LIMIT);
  }

  @Test
  public void set_max_limit() throws Exception {
    options.setMaxLimit();
    assertThat(options.getLimit()).isEqualTo(QueryContext.MAX_LIMIT);
  }

  @Test
  public void max_page_size() throws Exception {
    options.setPage(3, QueryContext.MAX_LIMIT + 10);
    assertThat(options.getOffset()).isEqualTo(QueryContext.MAX_LIMIT * 2);
    assertThat(options.getLimit()).isEqualTo(QueryContext.MAX_LIMIT);
  }

  @Test
  public void getFieldsToReturn() throws Exception {
    assertThat(options.getFieldsToReturn()).isEmpty();

    options.setFieldsToReturn(Arrays.asList("one", "two"));
    assertThat(options.getFieldsToReturn()).containsOnly("one", "two");

    options.addFieldsToReturn(Arrays.asList("three"));
    assertThat(options.getFieldsToReturn()).containsOnly("one", "two", "three");

    options.addFieldsToReturn("four");
    assertThat(options.getFieldsToReturn()).containsOnly("one", "two", "three", "four");
  }

  @Test
  public void support_immutable_fields() throws Exception {
    options.setFieldsToReturn(ImmutableList.of("one", "two"));
    assertThat(options.getFieldsToReturn()).containsOnly("one", "two");

    options.addFieldsToReturn(ImmutableList.of("three"));
    assertThat(options.getFieldsToReturn()).containsOnly("one", "two", "three");

    options.addFieldsToReturn("four");
    assertThat(options.getFieldsToReturn()).containsOnly("one", "two", "three", "four");
  }

  @Test
  public void do_not_request_facets_by_default() throws Exception {
    assertThat(options.isFacet()).isFalse();

    options.addFacets(Arrays.asList("polop"));
    assertThat(options.isFacet()).isTrue();
  }
}
