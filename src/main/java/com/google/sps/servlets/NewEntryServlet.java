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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.sps.OAuthUtils;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

// cloud language imports
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

/** Servlet responsible for creating new tasks. */
@WebServlet("/new-entry")
public class NewEntryServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Sanitize user input to remove HTML tags and JavaScript.
    String entryTitle = Jsoup.clean(request.getParameter("entryTitle"), Whitelist.none());
    String entryText = Jsoup.clean(request.getParameter("entryText"), Whitelist.none());
    long timestamp = System.currentTimeMillis();

    String sessionId = request.getSession().getId();
    Userinfo userInfo = null;
    boolean isUserLoggedIn =
        OAuthUtils.isUserLoggedIn(sessionId);

    if (isUserLoggedIn) {
      userInfo = OAuthUtils.getUserInfo(sessionId);
    }
    else {
        response.sendRedirect("/login");
    }

    // performing sentiment analysis
    String message = request.getParameter("entryText");
    Document doc =
    Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float s = sentiment.getScore();
    // score ranges from [-1,1], with a precision point of 0.1 
    // score < 0: more negative; score > 0: more positive
    double score = s;
    System.out.println(s);
    languageService.close();
    
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Entry");
    FullEntity entryEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("entryTitle", entryTitle)
            .set("entryText", entryText)
            .set("timestamp", timestamp)
            .set("userId", userInfo.getEmail())
            .set("score", score)
            .build();
    datastore.put(entryEntity);
    

    response.sendRedirect("/homepage.html");
  }
}
