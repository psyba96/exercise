document.addEventListener('DOMContentLoaded', function () {
    const inputName = document.getElementById('searchInputName');
    const inputMuscle = document.getElementById('searchInputMuscle');
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
        const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';
        const response = await fetch(`psyba/api/exercise${queryString}`);
        const data = await response.json();

        render(data);
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
            div.innerHTML = `
                <h3>${ex.name}</h3>
                <p><strong>Primary Muscles:</strong> ${ex.primaryMuscles || 'N/A'}</p>
                <p><strong>Instructions:</strong> ${ex.instructions || 'N/A'}</p>
                <p><strong>Equipment Needed:</strong> ${ex.equipment || 'None'}</p>
            `;
            results.appendChild(div);
        });
    };

    //[inputName, inputMuscle].forEach(input => input.addEventListener('input', fetchExercises));
    searchButton.addEventListener('click', fetchExercises); // Search button trigger

});