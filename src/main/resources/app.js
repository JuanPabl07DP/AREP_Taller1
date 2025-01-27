// Cargar todas las películas
function loadAllMovies() {
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        document.getElementById("getAllMovies").innerHTML = this.responseText;
    }
    xhttp.open("GET", "/api/movies");
    xhttp.send();
}

// Añadir película
function addMovie() {
    const title = document.getElementById("movieTitle").value;
    const director = document.getElementById("movieDirector").value;
    const year = document.getElementById("movieYear").value;

    const url = `/api/movies?title=${encodeURIComponent(title)}&director=${encodeURIComponent(director)}&year=${encodeURIComponent(year)}`;

    fetch(url, {method: 'POST'})
        .then(response => response.text())
        .then(result => {
            document.getElementById("postMovieResponse").innerHTML = result;
            loadAllMovies();
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById("postMovieResponse").innerHTML = "Error al añadir la película";
        });
}

// Actualizar película
function updateMovie() {
    const id = document.getElementById("movieId").value;
    const title = document.getElementById("updateTitle").value;
    const director = document.getElementById("updateDirector").value;
    const year = document.getElementById("updateYear").value;

    const url = `/api/movies?id=${encodeURIComponent(id)}&title=${encodeURIComponent(title)}&director=${encodeURIComponent(director)}&year=${encodeURIComponent(year)}`;

    fetch(url, {method: 'PUT'})
        .then(response => response.text())
        .then(result => {
            document.getElementById("putMovieResponse").innerHTML = result;
            loadAllMovies();
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById("putMovieResponse").innerHTML = "Error al actualizar la película";
        });
}

// Eliminar película
function deleteMovie() {
    const id = document.getElementById("deleteMovieId").value;

    fetch(`/api/movies?id=${encodeURIComponent(id)}`, {method: 'DELETE'})
        .then(response => response.text())
        .then(result => {
            document.getElementById("deleteMovieResponse").innerHTML = result;
            loadAllMovies();
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById("deleteMovieResponse").innerHTML = "Error al eliminar la película";
        });
}

// Cargar películas al iniciar la página
document.addEventListener('DOMContentLoaded', loadAllMovies);