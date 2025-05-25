document.addEventListener('DOMContentLoaded', function () {
    const inputName = document.getElementById('searchInputName');
    const inputMuscle = document.getElementById('searchInputMuscle');
    const inputLevel = document.getElementById('searchLevel');
    const results = document.getElementById('results');
    const searchButton = document.getElementById('searchButton'); // Adding search button trigger
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
    const toTitleCase = (str) => {
        if (!str || typeof str !== 'string') return '';
        return str
            .toLowerCase()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
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
                <p><strong>Primary Muscles:</strong> ${ex.primaryMuscles.map(muscle => toTitleCase(muscle)).join(', ') || 'N/A'}</p>
                <p><strong>Secondary Muscles:</strong> ${ex.secondaryMuscles.map(muscle => toTitleCase(muscle)).join(', ') || 'N/A'}</p>
                <p><strong>Equipment Needed:</strong> ${toTitleCase(ex.equipment) || 'None'}</p>
                <p><strong>Instructions:</strong></p>
                <details>
                    <summary>Click to view instructions</summary>
                    ${instructionsList}
                </details>
            `;
            results.appendChild(div);
        });
    };
    searchButton.addEventListener('click', fetchExercises); // Search button trigger
});