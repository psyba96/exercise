document.addEventListener('DOMContentLoaded', () => {
  const inputName = document.getElementById('searchInputName');
  const inputMuscle = document.getElementById('searchInputMuscle');
  const inputLevel = document.getElementById('searchLevel');
  const muscleGroupInput = document.getElementById('muscleGroupInput');
  const results = document.getElementById('results');
  const searchButton = document.getElementById('searchButton');
  const searchButtonAi = document.getElementById('searchButtonAi');

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

      if (inputName.value) {
        postBody.name = encodeURIComponent(inputName.value);
      }
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

      const instructionsList = Array.isArray(exercise.instructions)
        ? `<ol>${exercise.instructions.map(inst => `<li>${inst}</li>`).join('')}</ol>`
        : exercise.instructions || 'N/A';

      div.innerHTML = `
        <h3> ${toTitleCase(exercise.name)}  </h3>
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
      results.appendChild(div);
    });
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
        <h3>${exercise.name}</h3>
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
      results.appendChild(div);
    });
  };

  searchButton.addEventListener('click', fetchExercises);
  searchButtonAi.addEventListener('click', fetchExercisesAi);
});
