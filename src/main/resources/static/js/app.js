let currentUserRoles = [];
const deleteModal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
const editModal = new bootstrap.Modal(document.getElementById('editUserModal'));
let currentUserToDelete = null;

$(document).ready(function () {
    loadCurrentUser();
    setupEventListeners();
});

function setupEventListeners() {
    $('#adminLink').click(handleAdminLinkClick);
    $('#userLink').click(handleUserLinkClick);
    $('#allUsersTab').click(handleAllUsersTabClick);
    $('#addUserTab').click(handleAddUserTabClick);

    // Формы
    $('#submitEditUser').click(handleEditUserSubmit);
    $('#addUserForm').submit(handleAddUserSubmit);
    $('#confirmDeleteBtn').click(handleDeleteConfirm);
}


function handleAdminLinkClick(e) {
    e.preventDefault();
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    $('.nav-link').removeClass('active');
    $(this).addClass('active');
    $('#userProfileContainer').hide();
    $('#adminPanelContainer').show();
    loadAllUsers();
    switchAdminTab('allUsersTab');
}

function handleUserLinkClick(e) {
    e.preventDefault();
    $('.nav-link').removeClass('active');
    $(this).addClass('active');
    $('#adminPanelContainer').hide();
    $('#userProfileContainer').show();
}

function handleAllUsersTabClick(e) {
    e.preventDefault();
    switchAdminTab('allUsersTab');
}

function handleAddUserTabClick(e) {
    e.preventDefault();
    switchAdminTab('addUserTab');
}

function handleEditUserSubmit() {
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    const formData = {
        id: $('#editUserId').val(),
        name: $('#editName').val(),
        secondname: $('#editSecondname').val(),
        username: $('#editUsername').val(),
        age: parseInt($('#editAge').val()),
        password: $('#editPassword').val(),
        roleIds: $('#editRolesSelect').val()
    };

    $.ajax({
        url: '/people/user',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        xhrFields: {withCredentials: true},
        success: function () {
            editModal.hide();
            showSuccessAlert('User updated successfully!');
            loadAllUsers();
        },
        error: function (xhr) {
            showErrorAlert('Error updating user: ' + xhr.statusText);
        }
    });
}

function handleAddUserSubmit(e) {
    e.preventDefault();
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    const formData = {
        name: $('input[name="name"]').val(),
        secondname: $('input[name="secondname"]').val(),
        username: $('input[name="username"]').val(),
        age: parseInt($('input[name="age"]').val()),
        password: $('input[name="password"]').val(),
        roleIds: $('#rolesSelect').val()
    };

    $.ajax({
        url: '/people/user',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        xhrFields: {withCredentials: true},
        success: function () {
            showSuccessAlert('User added successfully!');
            $('#addUserForm')[0].reset();
            switchAdminTab('allUsersTab');
            loadAllUsers();
        },
        error: function (xhr) {
            showErrorAlert('Error adding user: ' + (xhr.responseJSON?.message || xhr.statusText));
        }
    });
}

function handleDeleteConfirm() {
    if (!currentUserRoles.includes('ROLE_ADMIN') || !currentUserToDelete) return;

    const userId = currentUserToDelete;
    $.ajax({
        url: '/people/' + userId,
        type: 'DELETE',
        xhrFields: {withCredentials: true},
        success: function () {
            deleteModal.hide();
            showSuccessAlert('User deleted successfully');
            loadAllUsers();
        },
        error: function (xhr) {
            deleteModal.hide();
            showErrorAlert('Error deleting user: ' + xhr.statusText);
        }
    });
}

function loadCurrentUser() {
    $.ajax({
        url: '/people/current',
        type: 'GET',
        dataType: 'json',
        xhrFields: {withCredentials: true},
        success: function (user) {
            updateCurrentUserUI(user);
            handleUserRoles(user.roles || []);
            updateNavigation();

            // Автоматически открываем админ-панель для администраторов
            if (currentUserRoles.includes('ROLE_ADMIN')) {
                $('#adminLink').trigger('click');
            } else {
                $('#userLink').trigger('click');
            }
        },
        error: function (xhr) {
            showErrorAlert('Error loading profile: ' + xhr.statusText);
        }
    });
}

function updateCurrentUserUI(user) {
    const tableBody = $('#currentUserTableBody');
    tableBody.empty();

    const rolesText = user.roles.map(role => role.name.replace('ROLE_', '')).join(', ');
    const row = `
    <tr>
        <td>${user.id}</td>
        <td>${user.name || '-'}</td>
        <td>${user.secondname || '-'}</td>
        <td>${user.username || '-'}</td>
        <td>${user.age || '-'}</td>
        <td>${rolesText}</td>
    </tr>`;

    tableBody.append(row);
    $('#currentUsername').text(user.username ? `${user.username} ` : '');
    $('#userRole').text(rolesText.length > 0 ? rolesText : 'No roles assigned');
}

function handleUserRoles(roles) {
    currentUserRoles = roles.map(role => role.name);

    if (currentUserRoles.includes('ROLE_ADMIN')) {
        $('#adminLinkContainer').show();
    } else {
        $('#adminLinkContainer').hide();
    }
}

function updateNavigation() {
    $('#userLink').show();

    $('.nav-link').removeClass('active');
    if (currentUserRoles.includes('ROLE_ADMIN')) {
        $('#adminLink').addClass('active');
    } else {
        $('#userLink').addClass('active');
    }
}

function switchAdminTab(tabId) {
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    $('#adminTabs .nav-link').removeClass('active');
    $('#' + tabId).addClass('active');

    if (tabId === 'allUsersTab') {
        $('#addUserContainer').hide();
        $('#adminTableContainer').show();
    } else if (tabId === 'addUserTab') {
        $('#adminTableContainer').hide();
        $('#addUserContainer').show();
        loadRoles();
    }
}

function loadAllUsers() {
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    $.ajax({
        url: '/people',
        type: 'GET',
        dataType: 'json',
        xhrFields: {withCredentials: true},
        success: function (users) {
            renderUsersTable(users);
        },
        error: function (xhr) {
            showErrorAlert('Error loading users: ' + xhr.statusText);
        }
    });
}

function renderUsersTable(users) {
    const tableBody = $('#userTableBody');
    tableBody.empty();

    $.each(users, function (index, user) {
        const roles = user.roles ? user.roles.map(role => role.name.replace('ROLE_', '')).join(', ') : 'No roles';
        const row = `
        <tr>
            <td>${user.id}</td>
            <td>${user.name || '-'}</td>
            <td>${user.secondname || '-'}</td>
            <td>${user.username || '-'}</td>
            <td>${user.age || '-'}</td>
            <td>${roles}</td>
            <td class="action-buttons">
                <button class="btn btn-sm btn-primary edit-btn" data-id="${user.id}">Edit</button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${user.id}">Delete</button>
            </td>
        </tr>`;

        tableBody.append(row);
    });

    $('.delete-btn').click(function () {
        const userId = $(this).data('id');
        showDeleteConfirmation(userId);
    });

    $('.edit-btn').click(function () {
        const userId = $(this).data('id');
        showEditForm(userId);
    });
}

function loadRoles() {
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    $.ajax({
        url: '/people/roles',
        type: 'GET',
        xhrFields: {withCredentials: true},
        success: function (roles) {
            const select = $('#rolesSelect');
            select.empty();
            roles.forEach(role => {
                select.append(`<option value="${role.id}">${role.name.replace('ROLE_', '')}</option>`);
            });
        },
        error: function (xhr) {
            showErrorAlert('Error loading roles: ' + xhr.statusText);
        }
    });
}

function showEditForm(userId) {
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    $.ajax({
        url: '/people/user?id=' + userId,
        type: 'GET',
        xhrFields: {withCredentials: true},
        success: function (user) {
            $('#editUserId').val(user.id);
            $('#editName').val(user.name);
            $('#editSecondname').val(user.secondname);
            $('#editUsername').val(user.username);
            $('#editAge').val(user.age);
            $('#editPassword').val('');
            loadRolesForEdit(user.roles.map(role => role.id));
            editModal.show();
        },
        error: function (xhr) {
            showErrorAlert('Error loading user data: ' + xhr.statusText);
        }
    });
}

function loadRolesForEdit(selectedRoleIds) {
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    $.ajax({
        url: '/people/roles',
        type: 'GET',
        xhrFields: {withCredentials: true},
        success: function (roles) {
            const select = $('#editRolesSelect');
            select.empty();

            roles.forEach(role => {
                const isSelected = selectedRoleIds.includes(role.id);
                const option = new Option(
                    role.name.replace('ROLE_', ''),
                    role.id,
                    isSelected,
                    isSelected
                );
                select.append(option);
            });
        },
        error: function (xhr) {
            showErrorAlert('Error loading roles: ' + xhr.statusText);
        }
    });
}

function showDeleteConfirmation(userId) {
    if (!currentUserRoles.includes('ROLE_ADMIN')) return;

    const userRow = $(`button.delete-btn[data-id="${userId}"]`).closest('tr');
    $('#modalUserId').val(userId);
    $('#modalUserFirstName').val(userRow.find('td:eq(1)').text());
    $('#modalUserLastName').val(userRow.find('td:eq(2)').text());
    $('#modalUserUsername').val(userRow.find('td:eq(3)').text());
    $('#modalUserAge').val(userRow.find('td:eq(4)').text());
    $('#modalUserRoles').val(userRow.find('td:eq(5)').text());
    currentUserToDelete = userId;
    deleteModal.show();
}

function showSuccessAlert(message) {
    const alert = $(`
        <div class="alert alert-success alert-auto-close alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `);
    $('#alerts-container').append(alert);
    setTimeout(() => alert.alert('close'), 3000);
}

function showErrorAlert(message) {
    const alert = $(`
        <div class="alert alert-danger alert-auto-close alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `);
    $('#alerts-container').append(alert);
    setTimeout(() => alert.alert('close'), 5000);
}