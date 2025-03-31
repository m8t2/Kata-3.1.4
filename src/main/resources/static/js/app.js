// app.js
document.addEventListener('DOMContentLoaded', function() {
    showAdmin(); // Автоматически показываем admin-панель при загрузке
});

function showAdmin() {
    document.getElementById('adminContent').style.display = 'block';
    document.getElementById('userContent').style.display = 'none';
    loadUsers(); // Загружаем пользователей при открытии Admin
}

function showUser() {
    document.getElementById('adminContent').style.display = 'none';
    document.getElementById('userContent').style.display = 'block';
}

function loadUsers() {
    const url = 'http://localhost:8080/people';

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(users => {
            renderUserTable(users);
        })
        .catch(error => {
            console.error('Error:', error);
            document.querySelector('#nav-home').innerHTML = '<div class="alert alert-danger">Error loading users</div>';
        });
}

function renderUserTable(users) {
    const tableContainer = document.querySelector('#nav-home');

    if (users.length === 0) {
        tableContainer.innerHTML = '<div class="alert alert-info">No users found</div>';
        return;
    }

    let tableHtml = `
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Roles</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;

    users.forEach(user => {
        tableHtml += `
            <tr>
                <td>${user.id}</td>
                <td>${user.username || ''}</td>
                <td>${user.email || ''}</td>
                <td>${user.roles ? user.roles.map(role => role.name.replace('ROLE_', '')).join(', ') : ''}</td>
                <td>
                    <button class="btn btn-sm btn-primary">Edit</button>
                    <button class="btn btn-sm btn-danger delete-btn" 
                            data-user-id="${user.id}" 
                            data-user-name="${user.username}" 
                            data-user-email="${user.email || ''}" 
                            data-user-roles="${user.roles ? user.roles.map(role => role.name.replace('ROLE_', '')).join(', ') : ''}">Delete</button>
                </td>
            </tr>
        `;
    });

    tableHtml += `
                </tbody>
            </table>
        </div>
    `;

    tableContainer.innerHTML = tableHtml;
    addDeleteEventListeners();
}

function addDeleteEventListeners() {
    const deleteButtons = document.querySelectorAll('.delete-btn');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            const userName = this.getAttribute('data-user-name');
            const userEmail = this.getAttribute('data-user-email');
            const userRoles = this.getAttribute('data-user-roles');

            // Заполняем поля модального окна
            document.getElementById('deleteUserId').value = userId;
            document.getElementById('deleteUserName').value = userName;
            document.getElementById('deleteUserEmail').value = userEmail;
            document.getElementById('deleteUserRoles').value = userRoles;

            // Показываем модальное окно
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
            deleteModal.show();
        });
    });

    document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
        const userId = this.getAttribute('data-user-id');
        deleteUser(userId);
    });
}

function deleteUser(userId) {
    const url = `http://localhost:8080/people/${userId}`;

    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteUserModal'));
            deleteModal.hide();
            loadUsers(); // Обновляем таблицу после удаления
            showAlert('Пользователь успешно удален', 'success');
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Ошибка при удалении пользователя', 'danger');
        });
}

function showAlert(message, type) {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    const tableContainer = document.querySelector('#nav-home');
    tableContainer.insertAdjacentHTML('afterbegin', alertHtml);

    setTimeout(() => {
        const alert = document.querySelector('.alert');
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 3000);
}