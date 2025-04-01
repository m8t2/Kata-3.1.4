$(document).ready(function() {
    // Обработчик для кнопки Admin
    $('#adminLink').click(function(e) {
        e.preventDefault();
        $(this).addClass('active');
        $('#userLink').removeClass('active');

        $.ajax({
            url: '/people',
            type: 'GET',
            dataType: 'json',
            success: function(users) {
                const tableBody = $('#userTableBody');
                tableBody.empty();

                $.each(users, function(index, user) {
                    const roles = user.authorities ?
                        user.authorities.map(auth => auth.authority).join(', ') :
                        'Нет данных о ролях';

                    const row = `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name || 'Нет данных'}</td>
                            <td>${user.secondname || 'Нет данных'}</td>
                            <td>${user.username || 'Нет данных'}</td>
                            <td>${user.password ? '••••••••' : 'Нет данных'}</td>
                            <td>${roles}</td>
                            <td>
                                <button class="btn btn-sm btn-primary">Edit</button>
                                <button class="btn btn-sm btn-danger">Delete</button>
                            </td>
                        </tr>
                    `;
                    tableBody.append(row);
                });

                $('#userTable').show();
            },
            error: function(xhr, status, error) {
                console.error('Ошибка загрузки пользователей:', error);
                alert('Ошибка: ' + (xhr.responseJSON?.message || error));
            }
        });
    });

    // Обработчик для кнопки User
    $('#userLink').click(function(e) {
        e.preventDefault();
        $(this).addClass('active');
        $('#adminLink').removeClass('active');
        $('#userTable').hide();
        // Здесь можно добавить загрузку данных для обычного пользователя
    });
});