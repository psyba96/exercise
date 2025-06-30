document.addEventListener('DOMContentLoaded', function () {
    const inputName = document.getElementById('searchInputName');
    const inputMuscle = document.getElementById('searchInputMuscle');
    const inputLevel = document.getElementById('searchLevel');
    const results = document.getElementById('results');
    const searchButton = document.getElementById('searchButton'); // Adding search button trigger
    const searchButtonAi = document.getElementById('searchButtonAi'); // Adding search button trigger Ai
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
    const renderPlan = (exercises) => {
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
                    <p><strong>sets:</strong> ${toTitleCase(ex.sets) || 'None'}</p>
                    <p><strong>reps:</strong> ${toTitleCase(ex.reps) || 'None'}</p>
                    <p><strong>duration:</strong> ${toTitleCase(ex.duration) || 'None'}</p>
                    <p><strong>Instructions:</strong></p>
                    <details>
                        <summary>Click to view instructions</summary>
                        ${instructionsList}
                    </details>
                `;
                results.appendChild(div);
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