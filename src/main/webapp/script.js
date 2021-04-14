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
  console.log('loaded entries!')
}

/** Creates an element that represents a task, including its delete button. */
function createEntryElement(entry) {
  const entryElement = document.createElement('li');
  entryElement.className = 'entry';

  const entryTitleElement = document.createElement('span');
  entryTitleElement.className = 'entryTitle';
  entryTitleElement.innerText = entry.entryTitle;

  const entryTextElement = document.createElement('span');
  entryTextElement.className = 'entryText';
  entryTextElement.innerText = entry.entryText;
  entryTextElement.addEventListener('click', () => {
    const entryTitleElement = document.getElementById('entryTitle');
    const entryContentElement = document.getElementById('entryContent');
    entryTitleElement.value = entry.entryTitle;
    entryContentElement.innerText = entry.entryText;
    console.log(entry.score);
  });

  const entryScoreElement = document.createElement('span');
  entryScoreElement.innerText = 'Score: ' + entry.score;
  entryScoreElement.style.fontStyle = 'italic';

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.className = 'btn btn-outline-primary';
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteEntry(entry);

    // Remove the task from the DOM.
    entryElement.remove();
  });

  entryElement.appendChild(entryTitleElement);
  entryElement.appendChild(entryTextElement);
  entryElement.appendChild(entryScoreElement);
  entryElement.appendChild(deleteButtonElement);
  return entryElement;
}

/** Tells the server to delete the task. */
function deleteEntry(entry) {
  const params = new URLSearchParams();
  params.append('id', entry.id);
  fetch('/delete-entry', {method: 'POST', body: params});
}


// google clouds API
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);
/** Creates a chart and adds it to the page. */
 async function drawChart() {
  const data = new google.visualization.DataTable();
  const response = await fetch('/scores');
  const scores = await response.json();
  let happy = 0;
  let neutral = 0;
  let sad = 0;
  for (let i = 0; i < scores.length; i ++){
    if (scores[i] > -0.2 && scores[i] < 0.2){
        neutral += 1
    } else if (scores[i] >= 0.2){
        happy += 1
    } else {
        sad += 1
    }
  }
  data.addColumn('string', 'Mood');
  data.addColumn('number', 'Count');
        data.addRows([
          ['Happy', happy],
          ['Neutral', neutral],
          ['Sad', sad],
        ]);

  const options = {
    'title': 'Daily Mood',
    'width':400,
    'height':400
  };

  const chart = new google.visualization.PieChart(
      document.getElementById('chart-container'));
  chart.draw(data, options);
}
