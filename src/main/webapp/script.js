// Copyright 2020 Google LLC
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

/** Fetches tasks from the server and adds them to the DOM. */
function login() {
    location.href = '/login_screen';
}
function loadEntries() {
  fetch('/list-entries').then(response => response.json()).then((entries) => {
    const entryListElement = document.getElementById('entry-list');
    entries.forEach((entry) => {
      entryListElement.appendChild(createEntryElement(entry));
    })
  });
  console.log("loaded entries!")
}

/** Creates an element that represents a task, including its delete button. */
function createEntryElement(entry) {
  const entryElement = document.createElement('li');
  entryElement.className = 'entry';
  entryElement.addEventListener("click", () => {
    const entryTitleElement = document.getElementById('entryTitle');
    const entryContentElement = document.getElementById('entryContent');
    entryTitleElement.value = entry.entryTitle;
    entryContentElement.innerText = entry.entryText;

  });

  const entryTitleElement = document.createElement('p');
  entryTitleElement.innerText = entry.entryTitle;
  entryTitleElement.style.fontWeight = "bold";

  const entryTextElement = document.createElement('span');
  entryTextElement.innerText = entry.entryText;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteEntry(entry);

    // Remove the task from the DOM.
    entryElement.remove();
  });

  entryElement.appendChild(entryTitleElement);
  entryElement.appendChild(entryTextElement);
  entryElement.appendChild(deleteButtonElement);
  return entryElement;
}

/** Tells the server to delete the task. */
function deleteEntry(entry) {
  const params = new URLSearchParams();
  params.append('id', entry.id);
  fetch('/delete-entry', {method: 'POST', body: params});
}
