document.addEventListener('DOMContentLoaded', function () {
    const inputName = document.getElementById('searchInputName');
    const inputMuscle = document.getElementById('searchInputMuscle');
    const inputLevel = document.getElementById('searchLevel');
    const results = document.getElementById('results');
    const searchButton = document.getElementById('searchButton'); // Adding search button trigger
    const searchButtonAi = document.getElementById('searchButtonAi'); // Adding search button trigger Ai
    const muscleGroup = document.getElementById('muscleGroupInput');
    const fetchExercises = async () => {
        const queryParams = [];
        if (inputName.value) {
            queryParams.push(`name=${encodeURIComponent(inputName.value)}`);
        }
        if (inputMuscle.value) {
            queryParams.push(`pMuscle=${encodeURIComponent(inputMuscle.value)}`);
        }
        if (inputLevel.value) {
                    queryParams.push(`level=${encodeURIComponent(inputLevel.value)}`);
                }
        const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';
        const response = await fetch(`psyba/api/exercise${queryString}`);
        const data = await response.json();

        render(data);
    };

    const fetchExercisesAi = async () => {
            const postBody = {};
            if (muscleGroupInput !== null && muscleGroupInput !== undefined && muscleGroupInput.value !== ""){
                postBody.muscle_group = muscleGroupInput.value;
            }
            else{
            postBody.muscle_group = inputMuscle.value;
            }
            if (inputName.value) {
                postBody.name=encodeURIComponent(inputName.value);
            }
            if (inputMuscle.value) {
                postBody.muscle=encodeURIComponent(inputMuscle.value);
            }
            if (inputLevel.value) {
                        postBody.level=encodeURIComponent(inputLevel.value);
                    }

            const response = await fetch(`psyba/api/exercise/prompt`, {
                                            method: 'POST',
                                            headers: {
                                                'Content-Type': 'application/json'
                                            },
                                            body: JSON.stringify(postBody)
                                        });

            const data = await response.json();

            renderPlan(data);
        };
    const toTitleCase = (str) => {
        if (!str || typeof str !== 'string') return '';
        return str
            .toLowerCase()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    };
    const renderPlan = (plan) => {
            results.innerHTML = "";
            if (!plan) {
                    results.innerHTML = "<p>No workout plan available.</p>";
                    return;
                }
            const sections = {
                    //"warm up": "Warm Up",
                    "warmUpExercises": "Warm Up",
                    //"workout": "Main Workout",
                    "mainExercises": "Main Workout",
                    //"cool down": "Cool Down"
                    "coolDownExercises": "Cool Down"

                };

             Object.entries(sections).forEach(([key, title]) => {
                    if (plan[key] && plan[key].length > 0) {
                        const sectionDiv = document.createElement('div');
                        sectionDiv.classList.add('workout-section');
                        sectionDiv.style.margin = "20px 0";

                        // Create collapsible section header
                        const details = document.createElement('details');
                        details.open = true; // Open by default

                        const summary = document.createElement('summary');
                        summary.innerHTML = `<h2>${title}</h2>`;
                        summary.style.cursor = "pointer";
                        details.appendChild(summary);

                        // Render exercises in this section
                        plan[key].forEach(ex => {
                            const exerciseDiv = document.createElement('div');
                            exerciseDiv.classList.add('exercise-card');
                            exerciseDiv.style.borderRadius = "12px";
                            exerciseDiv.style.border = "1px solid #ccc";
                            exerciseDiv.style.padding = "10px";
                            exerciseDiv.style.margin = "10px 0";

                            const instructionsList = Array.isArray(ex.instructions)
                                ? `<ol>${ex.instructions.map(inst => `<li>${inst}</li>`).join('')}</ol>`
                                : ex.instructions || 'N/A';

                            exerciseDiv.innerHTML = `
                                <h3>${ex.name}</h3>
                                <div class="exercise-gif" style="margin: 10px 0; text-align: center;">

                                    <img src="https://exercise-demo-gifs.s3.us-east-1.amazonaws.com/${ex.gifHash}.gif"
                                    alt="${ex.name} demonstration"
                                    style="max-width: 100%; height: auto; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"
                                    onerror="this.style.display='none'; this.nextElementSibling.style.display='block';"
                                    />
                                    <div style="display: none; padding: 20px; background: #f5f5f5; border-radius: 8px; color: #666;">
                                    <p>GIF demonstration not available</p>
                                    </div>
                                </div>
                                <p><strong>Sets:</strong> ${toTitleCase(ex.sets) || 'None'}</p>
                                <p><strong>Reps:</strong> ${toTitleCase(ex.reps) || 'None'}</p>
                                <p><strong>Duration:</strong> ${toTitleCase(ex.duration) || 'None'}</p>
                                <p><strong>Instructions:</strong></p>
                                <details>
                                    <summary>Click to view instructions</summary>
                                    ${instructionsList}
                                </details>
                            `;
                            details.appendChild(exerciseDiv);
                        });

                        sectionDiv.appendChild(details);
                        results.appendChild(sectionDiv);
                    }
                });

        };
    const render = (exercises) => {
        results.innerHTML = "";
        if (exercises.length === 0) {
            results.innerHTML = "<p>No results found.</p>";
            return;
        }
        exercises.forEach(ex => {
            const div = document.createElement('div');
            div.classList.add('exercise-card');
            div.style.borderRadius = "12px"; // Adding rounded corners
            div.style.border = "1px solid #ccc"; // Optional: Adding border
            div.style.padding = "10px"; // Optional: Adding padding for better appearance
            div.style.margin = "10px 0"; // Optional: Adding margin between cards
            const instructionsList = Array.isArray(ex.instructions) 
                ? `<ol>${ex.instructions.map(inst => `<li>${inst}</li>`).join('')}</ol>` 
                : ex.instructions || 'N/A';
            div.innerHTML = `
                <h3>${ex.name}</h3>
                <p><strong>Level:</strong> ${toTitleCase(ex.level) || 'None'}</p>
                <p><strong>Primary Muscles:</strong> ${ex.primaryMuscles?.map(muscle =>
                    muscle?.startsWith('{{') ? 'None' : toTitleCase(muscle.replace(/_/g, ' '))).join(', ') || 'N/A'}</p>
                <p><strong>Secondary Muscles:</strong> ${ex.secondaryMuscles?.map(muscle =>
                    muscle?.startsWith('{{') ? 'None' : toTitleCase(muscle.replace(/_/g, ' '))).join(', ') || 'N/A'}</p>
                <p><strong>Equipment Needed:</strong> ${ex.equipment?.startsWith('{{') ? 'None' : toTitleCase(ex.equipment) || 'None'}</p>
                <p><strong>Force:</strong> ${ex.force?.startsWith('{{') ? 'None' : toTitleCase(ex.force) || 'None'}</p>
                <p><strong>Mechanic:</strong> ${ex.mechanic?.startsWith('{{') ? 'None' : toTitleCase(ex.mechanic) || 'None'}</p>
                <p><strong>Instructions:</strong></p>
                <details>
                    <summary>Click to view instructions</summary>
                    ${instructionsList}
                </details>
            `;
            results.appendChild(div);
        });
    };
    searchButton.addEventListener('click', fetchExercises);
    searchButtonAi.addEventListener('click', fetchExercisesAi);// Search button trigger


});