// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.sps.OAuthUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for returning list of scores. */
@WebServlet("/scores")
public class ScoreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    String sessionId = request.getSession().getId();
    Userinfo userInfo = null;
    boolean isUserLoggedIn =
        OAuthUtils.isUserLoggedIn(sessionId);

    if (isUserLoggedIn) {
      userInfo = OAuthUtils.getUserInfo(sessionId);
    }
    else {
        response.getWriter().println("[]");
        return;
    }

    Query<Entity> query =
        Query.newEntityQueryBuilder()
        .setKind("Entry")
        .setFilter(PropertyFilter.eq("userId", userInfo.getEmail()))
        .build();
    QueryResults<Entity> results = datastore.run(query);

    List<Double> scores = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();
      double score = entity.getDouble("score");
      scores.add(score);
    }
    // String json = JSON.stringify(scores);
    System.out.println(scores);
    response.setContentType("text/html;");
    response.getWriter().println(scores);
  }
}
