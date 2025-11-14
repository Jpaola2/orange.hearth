/* ========================================= */
/* 1. Gestión de Interfaz (De script.js) */
/* ========================================= */

// Función para mostrar el contenido de una sección específica del dashboard
function showContent(sectionId) {
    // Oculta todas las secciones
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.style.display = 'none';
    });

    // Muestra la sección deseada
    const activeSection = document.getElementById(sectionId);
    if (activeSection) {
        activeSection.style.display = 'block';
    }

    // Actualiza la visualización de la gestión de usuarios/veterinarios si aplica
    if (sectionId === 'gestion-usuarios') {
        cargarTablaUsuarios();
        cargarTablaVeterinarios();
    }
    
    // Si la sección es la de reportes, genera el gráfico (si es que existe)
    if (sectionId === 'reportes') {
        // Llama a la función de inicialización de gráficos si existe en esta página
        if (typeof initDashboardCharts === 'function') {
            initDashboardCharts();
        }
    }
}

// Inicialización de la navegación (al cargar el DOM)
document.addEventListener('DOMContentLoaded', () => {
    // Por defecto, muestra el dashboard principal o la primera sección
    showContent('dashboard-principal'); 

    // Opcional: Manejo de la barra lateral si hay un menú
    const sidebarItems = document.querySelectorAll('.sidebar ul li');
    sidebarItems.forEach(item => {
        item.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            if (targetId) {
                showContent(targetId);
            }
        });
    });
});

// Manejo de modales
function showModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex'; // Usamos flex para centrado
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
    }
}


/* ========================================= */
/* 2. Lógica de Datos y Persistencia (De app.js) */
/* ========================================= */

// --- Usuarios (Tutores) ---
const KEY_USUARIOS = 'usuarios';

function cargarUsuarios() {
    try {
        const usuarios = localStorage.getItem(KEY_USUARIOS);
        return usuarios ? JSON.parse(usuarios) : [];
    } catch (e) {
        console.error("Error al cargar usuarios:", e);
        return [];
    }
}

function guardarUsuario(nuevoUsuario) {
    const usuarios = cargarUsuarios();
    
    // Añade el estado 'activo' por defecto si no existe
    nuevoUsuario.estado = 'activo';
    
    // Prevenir duplicados (ej. por correo)
    const index = usuarios.findIndex(u => u.correo === nuevoUsuario.correo);
    if (index === -1) {
        usuarios.push(nuevoUsuario);
        localStorage.setItem(KEY_USUARIOS, JSON.stringify(usuarios));
        return true;
    }
    return false; // Usuario ya existe
}

// --- Veterinarios ---
const KEY_VETERINARIOS = 'veterinarios';

function cargarVeterinarios() {
    try {
        const veterinarios = localStorage.getItem(KEY_VETERINARIOS);
        return veterinarios ? JSON.parse(veterinarios) : [];
    } catch (e) {
        console.error("Error al cargar veterinarios:", e);
        return [];
    }
}

function guardarVeterinario(nuevoVeterinario) {
    const veterinarios = cargarVeterinarios();
    
    // Añade el estado 'activo' por defecto si no existe
    nuevoVeterinario.estado = 'activo';
    
    // Prevenir duplicados (ej. por tarjeta profesional)
    const index = veterinarios.findIndex(v => v.tarjeta_profesional === nuevoVeterinario.tarjeta_profesional);
    if (index === -1) {
        veterinarios.push(nuevoVeterinario);
        localStorage.setItem(KEY_VETERINARIOS, JSON.stringify(veterinarios));
        return true;
    }
    return false; // Veterinario ya existe
}

// --- Gráficos (Simulados) ---
function initDashboardCharts() {
    // Esta función debe inicializar tus gráficos usando Chart.js (o similar).
    // La dejaremos vacía o con un ejemplo simple, ya que el código de Chart.js no fue provisto.
    const ctx = document.getElementById('myChart'); 
    if (ctx) {
        console.log("Inicializando Chart.js en el contexto del Dashboard...");
        // Ejemplo de inicialización:
        /*
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Enero', 'Febrero', 'Marzo'],
                datasets: [{
                    label: 'Citas Realizadas',
                    data: [12, 19, 3],
                    backgroundColor: 'rgba(252, 177, 37, 0.5)'
                }]
            }
        });
        */
    }
}


/* ========================================= */
/* 3. Validación y Mejoras (Para Registro/Login) */
/* ========================================= */

/**
 * Valida que un campo de correo tenga contenido y el formato @.
 * @param {HTMLInputElement} input - El campo de entrada de correo.
 * @returns {boolean} - True si es válido, False si no.
 */
function validateEmailField(input) {
    const value = input.value.trim();
    // Verifica campo lleno y que contenga el símbolo @
    if (!value || !value.includes('@') || !/\S+@\S+\.\S+/.test(value)) {
        return false;
    }
    return true;
}

// Escuchador de eventos para validación en el formulario de REGISTRO
document.addEventListener('DOMContentLoaded', () => {
    const formRegistro = document.getElementById('form-registro-usuario'); // Asume este ID en registro.html

    if (formRegistro) {
        formRegistro.addEventListener('submit', function(e) {
            let isFormValid = true;
            
            const requiredFields = formRegistro.querySelectorAll('input[required], select[required]');
            
            requiredFields.forEach(field => {
                // 1. Validación de campo vacío (requerido por HTML)
                if (!field.value.trim()) {
                    isFormValid = false;
                    field.style.border = '1px solid red';
                } else {
                    field.style.border = '';
                }
                
                // 2. Validación específica para el correo
                if (field.type === 'email' && !validateEmailField(field)) {
                    isFormValid = false;
                    field.style.border = '1px solid red';
                    alert(`El campo ${field.placeholder || field.name} debe ser un correo electrónico válido con el símbolo @.`);
                }
            });
            
            if (!isFormValid) {
                e.preventDefault();
                alert('Por favor, complete y corrija todos los campos obligatorios.');
            } else {
                // Si la validación es exitosa, se puede llamar a guardarUsuario/guardarVeterinario
                // Aquí iría la lógica de registro real
                console.log("Formulario de registro validado. Procediendo a enviar datos...");
            }
        });
    }
});
/* ========================================= */
/* 4. Lógica de Gestión de Administrador (NUEVO) */
/* ========================================= */

// --- Renderizado y Filtro de Tutores (Usuarios) ---

// Función principal para cargar y filtrar la tabla de usuarios (Tutores)
function cargarTablaUsuarios() {
    const tableBody = document.getElementById('users-table-body');
    if (!tableBody) return; // Si no estamos en el Dashboard Admin, salir

    // Obtener los datos actuales de los filtros
    const searchTerm = document.getElementById('filter-user-search')?.value.toLowerCase() || '';
    const statusFilter = document.getElementById('filter-user-status')?.value || 'all';

    let usuarios = cargarUsuarios(); // Función ya existente

    // 1. Filtrar
    const filteredUsers = usuarios.filter(user => {
        const matchesSearch = user.nombre.toLowerCase().includes(searchTerm) || 
                              user.correo.toLowerCase().includes(searchTerm);
        
        const matchesStatus = statusFilter === 'all' || user.estado === statusFilter;
        
        return matchesSearch && matchesStatus;
    });

    // 2. Renderizar
    tableBody.innerHTML = '';
    
    if (filteredUsers.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 20px;">No se encontraron tutores que coincidan con los filtros.</td></tr>';
        return;
    }
    
    filteredUsers.forEach(user => {
        const row = tableBody.insertRow();
        row.innerHTML = `
            <td style="padding: 10px; border: 1px solid #ddd;">${user.nombre}</td>
            <td style="border: 1px solid #ddd;">${user.correo}</td>
            <td style="border: 1px solid #ddd;">${user.cedula || 'N/A'}</td>
            <td style="border: 1px solid #ddd;">${user.telefono || 'N/A'}</td>
            <td style="border: 1px solid #ddd;">
                <span style="padding: 5px; border-radius: 5px; background-color: ${user.estado === 'activo' ? 'var(--verde)' : 'var(--rojo)'}; color: white;">
                    ${user.estado.toUpperCase()}
                </span>
            </td>
            <td style="text-align: center; border: 1px solid #ddd;">
                <button onclick="toggleUserStatus('${user.correo}')" style="background-color: ${user.estado === 'activo' ? 'var(--rojo)' : 'var(--verde)'}; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer;">
                    ${user.estado === 'activo' ? 'Desactivar' : 'Activar'}
                </button>
            </td>
        `;
    });
}

// Función para alternar el estado de un usuario (Tutor)
function toggleUserStatus(correo) {
    let usuarios = cargarUsuarios();
    const index = usuarios.findIndex(u => u.correo === correo);

    if (index !== -1) {
        // Cambia el estado
        usuarios[index].estado = usuarios[index].estado === 'activo' ? 'inactivo' : 'activo';
        // Guarda el array actualizado
        localStorage.setItem(KEY_USUARIOS, JSON.stringify(usuarios));
        alert(`Estado del tutor ${usuarios[index].nombre} actualizado a: ${usuarios[index].estado.toUpperCase()}`);
        
        // Vuelve a cargar la tabla para reflejar el cambio
        cargarTablaUsuarios();
    }
}

// Llama a la función de filtro (usada en el onkeyup/onchange del HTML)
function filterUsers() {
    cargarTablaUsuarios();
}

// --- Renderizado y Filtro de Veterinarios ---

// Función principal para cargar y filtrar la tabla de veterinarios
function cargarTablaVeterinarios() {
    const tableBody = document.getElementById('vets-table-body');
    if (!tableBody) return; 

    const searchTerm = document.getElementById('filter-vet-search')?.value.toLowerCase() || '';
    const statusFilter = document.getElementById('filter-vet-status')?.value || 'all';

    let veterinarios = cargarVeterinarios(); // Función ya existente

    // 1. Filtrar
    const filteredVets = veterinarios.filter(vet => {
        const matchesSearch = vet.nombre.toLowerCase().includes(searchTerm) || 
                              vet.tarjeta_profesional.toLowerCase().includes(searchTerm) || 
                              vet.correo.toLowerCase().includes(searchTerm);
        
        const matchesStatus = statusFilter === 'all' || vet.estado === statusFilter;
        
        return matchesSearch && matchesStatus;
    });

    // 2. Renderizar
    tableBody.innerHTML = '';
    
    if (filteredVets.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 20px;">No se encontraron veterinarios que coincidan con los filtros.</td></tr>';
        return;
    }

    filteredVets.forEach(vet => {
        const row = tableBody.insertRow();
        row.innerHTML = `
            <td style="padding: 10px; border: 1px solid #ddd;">${vet.nombre}</td>
            <td style="border: 1px solid #ddd;">${vet.correo}</td>
            <td style="border: 1px solid #ddd;">${vet.tarjeta_profesional}</td>
            <td style="border: 1px solid #ddd;">${vet.especialidad || 'N/A'}</td>
            <td style="border: 1px solid #ddd;">
                <span style="padding: 5px; border-radius: 5px; background-color: ${vet.estado === 'activo' ? 'var(--verde)' : 'var(--rojo)'}; color: white;">
                    ${vet.estado.toUpperCase()}
                </span>
            </td>
            <td style="text-align: center; border: 1px solid #ddd;">
                <button onclick="toggleVetStatus('${vet.tarjeta_profesional}')" style="background-color: ${vet.estado === 'activo' ? 'var(--rojo)' : 'var(--verde)'}; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer;">
                    ${vet.estado === 'activo' ? 'Desactivar' : 'Activar'}
                </button>
            </td>
        `;
    });
}

// Función para alternar el estado de un veterinario
function toggleVetStatus(tarjeta_profesional) {
    let veterinarios = cargarVeterinarios();
    const index = veterinarios.findIndex(v => v.tarjeta_profesional === tarjeta_profesional);

    if (index !== -1) {
        veterinarios[index].estado = veterinarios[index].estado === 'activo' ? 'inactivo' : 'activo';
        localStorage.setItem(KEY_VETERINARIOS, JSON.stringify(veterinarios));
        alert(`Estado del Dr/a. ${veterinarios[index].nombre} actualizado a: ${veterinarios[index].estado.toUpperCase()}`);
        cargarTablaVeterinarios();
    }
}

// Llama a la función de filtro (usada en el onkeyup/onchange del HTML)
function filterVeterinarios() {
    cargarTablaVeterinarios();
}


// --- Lógica de Registro de Veterinario (Modal) ---
document.addEventListener('DOMContentLoaded', () => {
    // Escucha el formulario de registro de veterinarios dentro del modal
    const formAddVet = document.getElementById('form-add-vet');
    if (formAddVet) {
        formAddVet.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const nuevoVeterinario = {
                nombre: formAddVet.nombre.value,
                correo: formAddVet.correo.value,
                especialidad: formAddVet.especialidad.value,
                tarjeta_profesional: formAddVet.tarjeta_profesional.value,
                telefono: formAddVet.telefono.value,
                password: formAddVet.password.value,
                // El estado 'activo' se añade dentro de guardarVeterinario
            };
            
            if (!validateEmailField(formAddVet.correo)) {
                alert('Por favor, ingrese un correo electrónico válido.');
                return;
            }

            const isSaved = guardarVeterinario(nuevoVeterinario); // Función ya existente
            
            if (isSaved) {
                alert(`✅ Veterinario ${nuevoVeterinario.nombre} registrado exitosamente. Contraseña inicial: ${nuevoVeterinario.password}`);
                closeModal('modal-add-vet');
                cargarTablaVeterinarios(); // Recarga la tabla de gestión
            } else {
                alert(`❌ Error: Ya existe un veterinario con la Tarjeta Profesional ${nuevoVeterinario.tarjeta_profesional}.`);
            }
        });
    }
});