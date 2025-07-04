document.addEventListener('DOMContentLoaded', () => {
  const inputName = document.getElementById('searchInputName');
  const inputMuscle = document.getElementById('searchInputMuscle');
  const inputLevel = document.getElementById('searchLevel');
  const muscleGroupInput = document.getElementById('muscleGroupInput');
  const results = document.getElementById('results');
  const searchButton = document.getElementById('searchButton');
  const searchButtonAi = document.getElementById('searchButtonAi');
  const loader = document.getElementById('loader');
  const errorDiv = document.getElementById('error');
  const modal = document.getElementById('demoModal');
  const modalImage = document.getElementById('demoModalImage');
  const closeModalBtn = document.getElementById('closeModalBtn');



  const toTitleCase = (str) => {
    if (!str || typeof str !== 'string') return '';
    return str
      .toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };

  const handleHttpError = async (response) => {
    if (!response.ok) {
      const errorText = await response.text();
      alert(`Error ${response.status}: ${errorText}`);
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
  };

  const fetchExercises = async () => {
    try {
      const queryParams = [];

      /*if (inputName.value) {
        queryParams.push(`name=${encodeURIComponent(inputName.value)}`);
      }*/
      if (inputMuscle.value) {
        queryParams.push(`pMuscle=${encodeURIComponent(inputMuscle.value)}`);
      }
      if (inputLevel.value) {
        queryParams.push(`level=${encodeURIComponent(inputLevel.value)}`);
      }

      const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';
      const response = await fetch(`psyba/api/exercise${queryString}`);

      await handleHttpError(response);

      const data = await response.json();
      render(data);
    } catch (error) {
      console.error('Fetch Exercises Error:', error);
    }
  };

  const fetchExercisesAi = async () => {
    try {
      const postBody = {};

      if (muscleGroupInput && muscleGroupInput.value) {
        postBody.prompt = muscleGroupInput.value;
      } else {
        postBody.prompt = inputMuscle.value;
      }

      /*if (inputName.value) {
        postBody.name = encodeURIComponent(inputName.value);
      }*/
      if (inputMuscle.value) {
        postBody.muscle = encodeURIComponent(inputMuscle.value);
      }
      if (inputLevel.value) {
        postBody.level = encodeURIComponent(inputLevel.value);
      }

      const response = await fetch(`psyba/api/exercise/prompt`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(postBody)
      });

      await handleHttpError(response);

      const data = await response.json();
      renderPlan(data.plan);
    } catch (error) {
      console.error('Fetch AI Exercises Error:', error);
    }
  };

  const renderPlan = (exercises) => {
    results.innerHTML = '';

    if (!exercises || exercises.length === 0) {
      results.innerHTML = '<p>No results found.</p>';
      return;
    }

    exercises.forEach((exercise) => {
      const div = document.createElement('div');
      div.classList.add('exercise-card');
      div.style.borderRadius = '12px';
      div.style.border = '1px solid #ccc';
      div.style.padding = '10px';
      div.style.margin = '10px 0';
      div.dataset.sets = exercise.set?.sets || '';
      div.dataset.reps = exercise.set?.reps || '';

      const instructionsList = Array.isArray(exercise.instructions)
        ? `<ol>${exercise.instructions.map(inst => `<li>${inst}</li>`).join('')}</ol>`
        : exercise.instructions || 'N/A';

      div.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h3 style="margin: 0;">${toTitleCase(exercise.name)}</h3>
          <div class="button-group" style="display: flex; gap: 0.5rem;">
                       <button class="refresh-btn" style="
                         padding: 0.3rem 0.6rem;
                         font-size: 0.8rem;
                         cursor: pointer;
                         border: none;
                         border-radius: 4px;
                         background-color: #f3f3f3;
                         color: #444;
                         box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                       ">🔄</button>
                       <button class="edit-btn" style="
                         padding: 0.3rem 0.6rem;
                         font-size: 0.8rem;
                         cursor: pointer;
                         border: none;
                         border-radius: 4px;
                         background-color: #f3f3f3;
                         color: #444;
                         box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                       ">✏️</button>
                     </div>
        </div>
        <div class="exercise-gif" style="margin: 10px 0; text-align: center;">
          <img
            src="https://exercise-demo-gifs.s3.us-east-1.amazonaws.com/${exercise.gifHash}.gif"
            alt="${exercise.name} demonstration"
            style="max-width: 100%; height: auto; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"
            onerror="this.style.display='none'; this.nextElementSibling.style.display='block';"
          />
          <div style="display: none; padding: 20px; background: #f5f5f5; border-radius: 8px; color: #666;">
            <p>GIF demonstration not available</p>
          </div>
        </div>
        <p><strong>Sets:</strong> ${toTitleCase(exercise.set?.sets) || 'None'}</p>
        <p><strong>Reps:</strong> ${toTitleCase(exercise.set?.reps) || 'None'}</p>
        <p><strong>Instructions:</strong></p>
        <details>
          <summary>Click to view instructions</summary>
          ${instructionsList}
        </details>
      `;
      attachEditHandler(div, exercise);
      results.appendChild(div);

      // Add Refresh button handler
                const refreshBtn = div.querySelector('.refresh-btn');
                refreshBtn.addEventListener('click', async () => {
                  refreshBtn.disabled = true;
                  refreshBtn.textContent = 'Refreshing...';

                  try {
                    const updatedExercise = await fetchSingleExercise(exercise);
                    updateExerciseCard(div, updatedExercise);
                  } catch (err) {
                    console.error('Error refreshing exercise:', err);
                    refreshBtn.textContent = 'Error - Try Again';
                  } finally {
                    setTimeout(() => {
                      refreshBtn.disabled = false;
                      refreshBtn.textContent = '🔄';
                    }, 1500);
                  }
                });



    });
    localStorage.setItem('lastExercises', JSON.stringify(exercises));
  };

  const render = (exercises) => {
    results.innerHTML = '';

    if (!exercises || exercises.length === 0) {
      results.innerHTML = '<p>No results found.</p>';
      return;
    }

    exercises.forEach((exercise) => {
      const div = document.createElement('div');
      div.classList.add('exercise-card');
      div.style.borderRadius = '12px';
      div.style.border = '1px solid #ccc';
      div.style.padding = '10px';
      div.style.margin = '10px 0';

      const instructionsList = Array.isArray(exercise.instructions)
        ? `<ol>${exercise.instructions.map(inst => `<li>${inst}</li>`).join('')}</ol>`
        : exercise.instructions || 'N/A';

      div.innerHTML = `
       <div style="display:flex; justify-content:space-between; align-items:center;">
           <h3 style="margin:0;">${toTitleCase(exercise.name)}</h3>
           <button class="view-demo-btn" style="
             padding:0.3rem 0.6rem;
             font-size:0.8rem;
             cursor:pointer;
             border:none;
             border-radius:4px;
             background-color:#4a7c7c;
             color:#fff;
           ">View Demo</button>
         </div>
        <p><strong>Level:</strong> ${toTitleCase(exercise.level) || 'None'}</p>
        <p><strong>Primary Muscles:</strong> ${
          exercise.primaryMuscles?.map(muscle =>
            muscle?.startsWith('{{') ? 'None' : toTitleCase(muscle.replace(/_/g, ' '))
          ).join(', ') || 'N/A'
        }</p>
        <p><strong>Secondary Muscles:</strong> ${
          exercise.secondaryMuscles?.map(muscle =>
            muscle?.startsWith('{{') ? 'None' : toTitleCase(muscle.replace(/_/g, ' '))
          ).join(', ') || 'N/A'
        }</p>
        <p><strong>Equipment Needed:</strong> ${
          exercise.equipment?.startsWith('{{') ? 'None' : toTitleCase(exercise.equipment) || 'None'
        }</p>
        <p><strong>Force:</strong> ${
          exercise.force?.startsWith('{{') ? 'None' : toTitleCase(exercise.force) || 'None'
        }</p>
        <p><strong>Mechanic:</strong> ${
          exercise.mechanic?.startsWith('{{') ? 'None' : toTitleCase(exercise.mechanic) || 'None'
        }</p>
        <p><strong>Instructions:</strong></p>
        <details>
          <summary>Click to view instructions</summary>
          ${instructionsList}
        </details>
      `;
      const viewDemoBtn = div.querySelector('.view-demo-btn');
      viewDemoBtn.addEventListener('click', () => {
        showDemoModal(exercise.gifHash);
      });
      results.appendChild(div);
    });
  };

  searchButton.addEventListener('click', fetchExercises);
  searchButtonAi.addEventListener('click', async () => {
    loader.style.display = 'block';
    errorDiv.style.display = 'none';
    errorDiv.textContent = '';

    try {
      await fetchExercisesAi(); // your real fetch function
    } catch (err) {
      console.error('AI fetch failed:', err);
      errorDiv.textContent = 'Oops! Something went wrong. Please try again later.';
      errorDiv.style.display = 'block';
    } finally {
      loader.style.display = 'none';
    }
  });

    const updateExerciseCard = (div, exercise) => {
      console.log("Updating exercise card:", {
        div,
        exercise
      });
      const sets = div.dataset.sets;
      const reps = div.dataset.reps;
      const instructionsList = Array.isArray(exercise.instructions)
        ? `<ol>${exercise.instructions.map(inst => `<li>${inst}</li>`).join('')}</ol>`
        : exercise.instructions || 'N/A';

      div.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h3 style="margin: 0;">${toTitleCase(exercise.name)}</h3>
         <div class="button-group" style="display: flex; gap: 0.5rem;">
             <button class="refresh-btn" style="
               padding: 0.3rem 0.6rem;
               font-size: 0.8rem;
               cursor: pointer;
               border: none;
               border-radius: 4px;
               background-color: #f3f3f3;
               color: #444;
               box-shadow: 0 1px 3px rgba(0,0,0,0.1);
             ">🔄</button>
             <button class="edit-btn" style="
               padding: 0.3rem 0.6rem;
               font-size: 0.8rem;
               cursor: pointer;
               border: none;
               border-radius: 4px;
               background-color: #f3f3f3;
               color: #444;
               box-shadow: 0 1px 3px rgba(0,0,0,0.1);
             ">✏️</button>
           </div>
        </div>
        <div class="exercise-gif" style="margin: 10px 0; text-align: center;">
          <img
            src="https://exercise-demo-gifs.s3.us-east-1.amazonaws.com/${exercise.gifHash}.gif"
            alt="${exercise.name} demonstration"
            style="max-width: 100%; height: auto; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"
            onerror="this.style.display='none'; this.nextElementSibling.style.display='block';"
          />
          <div style="display: none; padding: 20px; background: #f5f5f5; border-radius: 8px; color: #666;">
            <p>GIF demonstration not available</p>
          </div>
        </div>
        <p><strong>Sets:</strong> ${sets} </p>
        <p><strong>Reps:</strong> ${reps} </p>
        <p><strong>Instructions:</strong></p>
        <details>
          <summary>Click to view instructions</summary>
          ${instructionsList}
        </details>
      `;
      attachEditHandler(div,exercise);
      // Re-attach the refresh handler
      const refreshBtn = div.querySelector('.refresh-btn');
      refreshBtn.addEventListener('click', async () => {
        refreshBtn.disabled = true;
        refreshBtn.textContent = 'Refreshing...';

        try {
          const updatedExercise = await fetchSingleExercise(exercise);
          updateExerciseCard(div, updatedExercise);
        } catch (err) {
          console.error('Error refreshing exercise:', err);
          refreshBtn.textContent = 'Error - Try Again';
        } finally {
          setTimeout(() => {
            refreshBtn.disabled = false;
            refreshBtn.textContent = '🔄';
          }, 1500);
        }
      });
    };

    const fetchSingleExercise = async (exerciseToBeUpdated) => {
      const saved = localStorage.getItem('lastExercises');
       if (saved) {
                const exercises = JSON.parse(saved);
                console.log('Restored exercises:', exercises);
              }
      const response = await fetch('psyba/api/exercise/singleExercise', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },


        body: JSON.stringify({ exerciseToBeUpdated: exerciseToBeUpdated.name,existingExercises:saved })
      });
        await handleHttpError(response);

      const data = await response.json();
      if (data && data.name && data.instructions) {
          return data; // Return the exercise object directly
        } else {
          throw new Error('Invalid exercise data returned');
        }
    };

    const attachEditHandler = (div, exercise) => {
      const editBtn = div.querySelector('.edit-btn');
      if (!editBtn) return;

      editBtn.addEventListener('click', () => {
        // Create modal overlay
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';

        // Create modal form
        const form = document.createElement('div');
        form.className = 'modal-form';

        form.innerHTML = `
          <h4 style="text-align: center; margin-top: 0;">Edit Exercise</h4>
          <select id="modal-category">
            <option value="">Select target muscle</option>
            <option value="CHEST">Chest</option>
                        <option value="TRAPS">Traps</option>
                        <option value="LATS">Lats</option>
                        <option value="NECK">Neck</option>
                        <option value="MIDDLE_BACK">Mid Back</option>
                        <option value="LOWER_BACK">Lower Back</option>
                        <option value="QUADRICEPS">Quads</option>
                        <option value="HAMSTRINGS">Hamstrings</option>
                        <option value="ADDUCTORS">Adductors</option>
                        <option value="ABDUCTORS">Abductors</option>
                        <option value="CALVES">Calves</option>
                        <option value="BICEPS">Biceps</option>
                        <option value="TRICEPS">Triceps</option>
                        <option value="FOREARMS">Forearms</option>
                        <option value="SHOULDERS">Shoulders</option>
                        <option value="ABDOMINALS">Abs</option>
          </select>
          <select id="modal-equipment">
          <option value="">Select equipment</option>
           <option value="roller">Roller</option>
           <option value="skierg machine">Skierg Machine</option>
           <option value="tire">Tire</option>
           <option value="stepmill machine">Stepmill Machine</option>
           <option value="hammer">Hammer</option>
           <option value="weighted">Weighted</option>
           <option value="dumbbell">Dumbbell</option>
           <option value="exercise ball">Exercise Ball</option>
           <option value="ez barbell">EZ Barbell</option>
           <option value="body only">Body Only</option>
           <option value="jump rope">Jump Rope</option>
           <option value="band">Band</option>
           <option value="cable">Cable</option>
           <option value="upper body ergometer">Upper Body Ergometer</option>
           <option value="wall">Wall</option>
           <option value="rope">Rope</option>
           <option value="elliptical machine">Elliptical Machine</option>
           <option value="medicine ball">Medicine Ball</option>
           <option value="kettlebell">Kettlebell</option>
           <option value="assisted">Assisted</option>
           <option value="towel">Towel</option>
           <option value="body weight">Body Weight</option>
           <option value="resistance band">Resistance Band</option>
           <option value="leverage machine">Leverage Machine</option>
           <option value="wheel roller">Wheel Roller</option>
           <option value="barbell">Barbell</option>
           <option value="bosu ball">Bosu Ball</option>
           <option value="smith machine">Smith Machine</option>
           <option value="sled machine">Sled Machine</option>
           <option value="stability ball">Stability Ball</option>
           <option value="olympic barbell">Olympic Barbell</option>
           <option value="trap bar">Trap Bar</option>
           <option value="stationary bike">Stationary Bike</option>
           <option value="tennis ball">Tennis Ball</option>
          </select>
          <input type="text" id="modal-keyword" class="search-input" placeholder="Enter keyword" />
          <div style="text-align: center;">
            <button id="modal-search">Find Options</button>
          </div>
          <div id="modal-results" style="margin-top:1rem; display:none;"></div>
        `;

        // Add modal to DOM
        overlay.appendChild(form);
        document.body.appendChild(overlay);

        // Dismiss modal on background click
        overlay.addEventListener('click', (e) => {
          if (e.target === overlay) {
            document.body.removeChild(overlay);
          }
        });

        // Search handler
        const searchBtn = form.querySelector('#modal-search');
        const categorySelect = form.querySelector('#modal-category');
        const equipmentSelect = form.querySelector('#modal-equipment');
        const keywordInput = form.querySelector('#modal-keyword');
        const resultsBox = form.querySelector('#modal-results');
        resultsBox.style.maxHeight = '250px';
        resultsBox.style.overflowY = 'auto';
        resultsBox.style.border = '1px solid #ddd';
        resultsBox.style.borderRadius = '6px';
        resultsBox.style.padding = '0.5rem';
        resultsBox.style.background = '#fff';

        searchBtn.addEventListener('click', async () => {
          resultsBox.style.display = 'block';
          const category = categorySelect.value;
          const equipment = equipmentSelect.value;
          const keyword = keywordInput.value.trim();
          const params = new URLSearchParams();

          if (category) params.append('pMuscle', category);
          if (equipment) params.append('equipment', equipment);
          if (keyword) params.append('name', keyword);

          resultsBox.innerHTML = 'Searching...';

          try {

                      const response = await fetch(`psyba/api/exercise?${params.toString()}`, {
                        method: 'GET'
                      });


            const data = await response.json();

            if (!Array.isArray(data) || data.length === 0) {
              resultsBox.innerHTML = 'No results found.';
              return;
            }

            resultsBox.innerHTML = '';
            data.forEach((item) => {
              const option = document.createElement('div');
                option.textContent = toTitleCase(item.name);
                option.style.padding = '0.5rem';
                option.style.border = '1px solid #ddd';
                option.style.borderRadius = '4px';
                option.style.marginBottom = '0.5rem';
                option.style.cursor = 'pointer';

                // Determine background color based on difficulty
                let bgColor = '#f9f9f9'; // default

                if (item.level) {
                  switch (item.level.toLowerCase()) {
                    case 'beginner':
                      bgColor = '#d6ff9a'; // light green
                      break;
                    case 'intermediate':
                      bgColor = '#ffde95'; // light yellow
                      break;
                    case 'advanced':
                      bgColor = '#ffa28d'; // light red
                      break;
                  }
                }

                option.style.backgroundColor = bgColor;
              option.addEventListener('click', () => {
                updateExerciseCard(div, item);
                document.body.removeChild(overlay);
              });

              resultsBox.appendChild(option);
            });
          } catch (err) {
            console.error('Error fetching options:', err);
            resultsBox.innerHTML = 'Error loading options.';
          }
        });
      });
    };

    const collectExercisesFromDOM = () => {
      const exerciseDivs = results.querySelectorAll('.exercise-card');
      const exerciseList = [];

      exerciseDivs.forEach((div) => {
        const nameEl = div.querySelector('h3');
        const name = nameEl ? nameEl.textContent.trim() : '';

        const setsText = div.querySelector('p strong')?.nextSibling?.textContent?.trim() || '';
        const repsText = div.querySelectorAll('p strong')[1]?.nextSibling?.textContent?.trim() || '';

        const instructionsDetails = div.querySelector('details');
        let instructions = [];

        if (instructionsDetails) {
          const ol = instructionsDetails.querySelector('ol');
          if (ol) {
            instructions = Array.from(ol.querySelectorAll('li')).map(li => li.textContent.trim());
          }
        }

        exerciseList.push({
          name,
          sets: setsText,
          reps: repsText,
          level:
          instructions
        });
      });

      return exerciseList;
    };

    const showDemoModal = (gifHash) => {
        modalImage.src = `https://exercise-demo-gifs.s3.us-east-1.amazonaws.com/${gifHash}.gif`;
        modal.style.display = 'flex';
      };

      closeModalBtn.addEventListener('click', () => {
        modal.style.display = 'none';
        modalImage.src = '';
      });

      modal.addEventListener('click', (e) => {
        if (e.target === modal) {
          modal.style.display = 'none';
          modalImage.src = '';
        }
      });
});
