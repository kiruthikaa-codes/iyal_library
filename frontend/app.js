// Use relative URL so it works locally AND on Railway
const API_URL = window.location.origin;
let currentUser = null;
let authToken = null;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
});

function checkAuth() {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');

    if (token && user) {
        authToken = token;
        currentUser = JSON.parse(user);

        // Restore last viewed page and tab
        const lastPage = localStorage.getItem('currentPage') || 'dashboard';
        const lastTab = localStorage.getItem('currentTab') || 'reading';

        if (lastPage === 'admin' && currentUser.role === 'ADMIN') {
            showAdminPage();
            const lastAdminTab = localStorage.getItem('currentAdminTab') || 'books';
            showAdminTab(lastAdminTab);
        } else {
            showDashboard(lastTab);
        }
    } else {
        showLogin();
    }
}

// Auth Functions
async function login(event) {
    event.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) throw new Error('Login failed');

        const data = await response.json();
        authToken = data.token;
        currentUser = { id: data.id, name: data.name, email: data.email, role: data.role };

        localStorage.setItem('token', authToken);
        localStorage.setItem('user', JSON.stringify(currentUser));

        // Show admin page if user is admin, otherwise dashboard
        if (currentUser.role === 'ADMIN') {
            showAdminPage();
        } else {
            showDashboard();
        }
    } catch (error) {
        alert('Login failed: ' + error.message);
    }
}

async function register(event) {
    event.preventDefault();
    const name = document.getElementById('registerName').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;

    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        if (!response.ok) throw new Error('Registration failed');

        const data = await response.json();
        authToken = data.token;
        currentUser = { id: data.id, name: data.name, email: data.email, role: data.role };
        
        localStorage.setItem('token', authToken);
        localStorage.setItem('user', JSON.stringify(currentUser));
        
        showDashboard();
    } catch (error) {
        alert('Registration failed: ' + error.message);
    }
}

function logout() {
    // Close dropdown if open
    const dropdown = document.getElementById('profileDropdown');
    if (dropdown) dropdown.classList.add('hidden');

    localStorage.removeItem('token');
    localStorage.removeItem('user');
    authToken = null;
    currentUser = null;
    showLogin();
}

function toggleProfileDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    dropdown.classList.toggle('hidden');
}

function showProfile() {
    // Close dropdown
    document.getElementById('profileDropdown').classList.add('hidden');

    // Show profile page (placeholder for now)
    alert('Profile page coming soon!');
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const profileBtn = document.getElementById('profileBtn');
    const dropdown = document.getElementById('profileDropdown');

    if (profileBtn && dropdown && !profileBtn.contains(event.target) && !dropdown.contains(event.target)) {
        dropdown.classList.add('hidden');
    }
});

// UI Functions
function showLogin() {
    document.getElementById('authForms').classList.remove('hidden');
    document.getElementById('loginForm').classList.remove('hidden');
    document.getElementById('registerForm').classList.add('hidden');
    document.getElementById('dashboard').classList.add('hidden');
    document.getElementById('authSection').classList.remove('hidden');
    document.getElementById('userSection').classList.add('hidden');
}

function showRegister() {
    document.getElementById('authForms').classList.remove('hidden');
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.remove('hidden');
    document.getElementById('dashboard').classList.add('hidden');
    document.getElementById('authSection').classList.remove('hidden');
    document.getElementById('userSection').classList.add('hidden');
}

async function showDashboard(initialTab = 'reading') {
    document.getElementById('authForms').classList.add('hidden');
    document.getElementById('dashboard').classList.remove('hidden');
    document.getElementById('adminPanel').classList.add('hidden');
    document.getElementById('authSection').classList.add('hidden');
    document.getElementById('userSection').classList.remove('hidden');
    document.getElementById('dashboardUserName').textContent = currentUser.name;

    // Save current page
    localStorage.setItem('currentPage', 'dashboard');

    // Show admin nav button for admins
    if (currentUser.role === 'ADMIN') {
        document.getElementById('adminNavBtn').classList.remove('hidden');
    } else {
        document.getElementById('adminNavBtn').classList.add('hidden');
    }

    // Load dashboard data
    await loadDashboardData();

    // Show the initial tab
    showReadingTab(initialTab);
}

async function loadDashboardData() {
    await Promise.all([
        loadMyReadingStats(),
        loadMyReadingBooks('READING'),
        loadAllBooks(),
        loadFamilyReading()
    ]);
}

async function loadMyReadingStats() {
    try {
        const response = await fetch(`${API_URL}/books/reading-status/my`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) return;

        const statuses = await response.json();
        const reading = statuses.filter(s => s.status === 'READING').length;
        const wantToRead = statuses.filter(s => s.status === 'WANT_TO_READ').length;
        const finished = statuses.filter(s => s.status === 'FINISHED').length;

        document.getElementById('statReading').textContent = reading;
        document.getElementById('statWantToRead').textContent = wantToRead;
        document.getElementById('statFinished').textContent = finished;
    } catch (error) {
        console.error('Error loading reading stats:', error);
    }
}

async function loadMyReadingBooks(status) {
    try {
        const response = await fetch(`${API_URL}/books/reading-status/filter?status=${status}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) return;

        const statuses = await response.json();

        let containerId;
        if (status === 'READING') containerId = 'readingContent';
        else if (status === 'WANT_TO_READ') containerId = 'wantToReadContent';
        else if (status === 'FINISHED') containerId = 'finishedContent';

        const container = document.getElementById(containerId);
        if (statuses.length === 0) {
            container.innerHTML = '<p class="text-text-secondary col-span-full text-center py-8">No books in this list yet</p>';
            return;
        }

        // Use simple=true for reading list tiles
        container.innerHTML = statuses.map(s => createBookCard({
            id: s.bookId,
            title: s.bookTitle,
            author: s.bookAuthor,
            coverImageUrl: s.bookCoverImageUrl,
            isAvailable: true
        }, true)).join('');
    } catch (error) {
        console.error('Error loading reading books:', error);
    }
}

async function loadAllBooks() {
    try {
        const response = await fetch(`${API_URL}/books`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to load books');

        const books = await response.json();
        displayBooks(books);
    } catch (error) {
        console.error('Error loading books:', error);
        document.getElementById('booksGrid').innerHTML = '<p class="text-text-secondary col-span-full text-center">No books yet. Add your first book!</p>';
    }
}

async function loadFamilyReading() {
    try {
        const response = await fetch(`${API_URL}/books/reading-status/currently-reading`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) return;

        const statuses = await response.json();
        const container = document.getElementById('familyReading');

        if (statuses.length === 0) {
            container.innerHTML = '<p class="text-text-secondary col-span-full text-center py-8">No one is reading anything yet</p>';
            return;
        }

        container.innerHTML = statuses.map(s => `
            <div class="bg-white rounded-lg shadow-sm hover:shadow-md transition overflow-hidden">
                <div class="aspect-[2/3] bg-bg-secondary flex items-center justify-center">
                    ${s.bookCoverImageUrl ?
                        `<img src="${s.bookCoverImageUrl}" alt="${s.bookTitle}" class="w-full h-full object-cover">` :
                        `<svg class="w-12 h-12 text-text-muted" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>`
                    }
                </div>
                <div class="p-2">
                    <h4 class="font-serif font-semibold text-sm line-clamp-2">${s.bookTitle}</h4>
                    <p class="text-xs text-text-secondary mt-0.5">${s.bookAuthor}</p>
                    <p class="text-xs text-accent-brown mt-1">${s.userName}</p>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading family reading:', error);
    }
}

function displayBooks(books) {
    const grid = document.getElementById('booksGrid');

    if (books.length === 0) {
        grid.innerHTML = '<p class="text-text-secondary col-span-full text-center py-12">No books yet. Add your first book to get started!</p>';
        return;
    }

    grid.innerHTML = books.map(book => createBookCard(book)).join('');
}

function createBookCard(book, simple = false) {
    if (simple) {
        // Simple tile for reading lists - just cover and title, no onclick
        return `
            <div class="bg-white rounded-lg shadow-sm hover:shadow-md transition overflow-hidden group relative">
                <div class="aspect-[2/3] bg-bg-secondary flex items-center justify-center">
                    ${book.coverImageUrl ?
                        `<img src="${book.coverImageUrl}" alt="${book.title}" class="w-full h-full object-cover">` :
                        `<svg class="w-12 h-12 text-text-muted" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>`
                    }
                </div>
                <div class="p-2">
                    <h4 class="font-serif font-semibold text-sm line-clamp-2">${book.title}</h4>
                    <p class="text-xs text-text-secondary mt-0.5">${book.author || ''}</p>
                </div>
                <button onclick="removeFromList(${book.id})" class="absolute top-1.5 right-1.5 bg-white bg-opacity-90 hover:bg-opacity-100 text-red-600 p-1.5 rounded-full shadow-md opacity-0 group-hover:opacity-100 transition">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                </button>
            </div>
        `;
    }

    // Detailed card for Browse All section
    return `
        <div class="bg-white rounded-lg shadow-sm hover:shadow-md transition overflow-hidden cursor-pointer" onclick="showBookDetail(${book.id})">
            <div class="aspect-[2/3] bg-bg-secondary flex items-center justify-center">
                ${book.coverImageUrl ?
                    `<img src="${book.coverImageUrl}" alt="${book.title}" class="w-full h-full object-cover">` :
                    `<svg class="w-12 h-12 text-text-muted" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>`
                }
            </div>
            <div class="p-3">
                <h4 class="font-serif font-semibold text-sm mb-1 line-clamp-2">${book.title}</h4>
                <p class="text-xs text-text-secondary mb-1">${book.author || ''}</p>
                <p class="text-xs text-text-muted mb-2">${book.genre || ''}</p>
                ${book.availableCount !== undefined ? `
                <div class="flex items-center justify-between text-xs">
                    <span class="${book.isAvailable ? 'text-green-600' : 'text-red-600'}">
                        ${book.isAvailable ? '● Available' : '● Borrowed'}
                    </span>
                    <span class="text-text-muted">${book.availableCount}/${book.totalCount}</span>
                </div>
                ` : ''}
            </div>
        </div>
    `;
}

function showAddBook() {
    document.getElementById('addBookForm').classList.remove('hidden');
}

function hideAddBook() {
    document.getElementById('addBookForm').classList.add('hidden');
    // Clear form
    const form = document.querySelector('#addBookForm form');
    if (form) form.reset();
}

// Reading Tabs
function showReadingTab(tab) {
    // Save current tab
    localStorage.setItem('currentTab', tab);

    // Update tab buttons
    document.getElementById('tabReading').className = 'px-6 py-3 font-medium text-text-secondary hover:text-accent-brown border-b-2 border-transparent';
    document.getElementById('tabWantToRead').className = 'px-6 py-3 font-medium text-text-secondary hover:text-accent-brown border-b-2 border-transparent';
    document.getElementById('tabFinished').className = 'px-6 py-3 font-medium text-text-secondary hover:text-accent-brown border-b-2 border-transparent';
    document.getElementById('tabBrowse').className = 'px-6 py-3 font-medium text-text-secondary hover:text-accent-brown border-b-2 border-transparent';

    // Hide all content
    document.getElementById('readingContent').classList.add('hidden');
    document.getElementById('wantToReadContent').classList.add('hidden');
    document.getElementById('finishedContent').classList.add('hidden');
    document.getElementById('browseContent').classList.add('hidden');

    // Show selected tab
    if (tab === 'reading') {
        document.getElementById('tabReading').className = 'px-6 py-3 font-medium text-accent-brown border-b-2 border-accent-brown';
        document.getElementById('readingContent').classList.remove('hidden');
        loadMyReadingBooks('READING');
    } else if (tab === 'wantToRead') {
        document.getElementById('tabWantToRead').className = 'px-6 py-3 font-medium text-accent-brown border-b-2 border-accent-brown';
        document.getElementById('wantToReadContent').classList.remove('hidden');
        loadMyReadingBooks('WANT_TO_READ');
    } else if (tab === 'finished') {
        document.getElementById('tabFinished').className = 'px-6 py-3 font-medium text-accent-brown border-b-2 border-accent-brown';
        document.getElementById('finishedContent').classList.remove('hidden');
        loadMyReadingBooks('FINISHED');
    } else if (tab === 'browse') {
        document.getElementById('tabBrowse').className = 'px-6 py-3 font-medium text-accent-brown border-b-2 border-accent-brown';
        document.getElementById('browseContent').classList.remove('hidden');
    }
}

// Book Detail Modal
let currentBookId = null;

async function showBookDetail(bookId) {
    currentBookId = bookId;

    try {
        const response = await fetch(`${API_URL}/books/${bookId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to load book');

        const book = await response.json();

        document.getElementById('modalBookTitle').textContent = book.title;
        document.getElementById('modalBookAuthor').textContent = `by ${book.author}`;
        document.getElementById('modalBookGenre').textContent = book.genre;
        document.getElementById('modalBookDescription').textContent = book.description || 'No description available.';
        document.getElementById('modalBookAvailability').textContent = `${book.availableCount} of ${book.totalCount} available`;

        // Show borrowing information if book is borrowed
        if (book.currentBorrowing) {
            document.getElementById('borrowedBySection').classList.remove('hidden');

            // Use borrowerName if available (external lending), otherwise show it's borrowed by the user
            const displayName = book.currentBorrowing.borrowerName || `${book.currentBorrowing.userName} (Family Member)`;
            document.getElementById('borrowedByName').textContent = displayName;
            document.getElementById('borrowedByContact').textContent = book.currentBorrowing.borrowerContact || '';
            const borrowedDate = new Date(book.currentBorrowing.borrowedAt).toLocaleDateString();
            document.getElementById('borrowedByDate').textContent = `Borrowed on ${borrowedDate}`;

            // Show return button if current user is the one who borrowed it (or admin)
            if (book.currentBorrowing.userId === currentUser.id || currentUser.role === 'ADMIN') {
                document.getElementById('returnBookBtn').classList.remove('hidden');
            } else {
                document.getElementById('returnBookBtn').classList.add('hidden');
            }

            // Disable borrow button if not available
            document.getElementById('borrowBtn').disabled = true;
            document.getElementById('borrowBtn').classList.add('opacity-50', 'cursor-not-allowed');
        } else {
            document.getElementById('borrowedBySection').classList.add('hidden');
            document.getElementById('returnBookBtn').classList.add('hidden');
            document.getElementById('borrowBtn').disabled = false;
            document.getElementById('borrowBtn').classList.remove('opacity-50', 'cursor-not-allowed');
        }

        if (book.coverImageUrl) {
            document.getElementById('modalBookCover').src = book.coverImageUrl;
            document.getElementById('modalBookCover').classList.remove('hidden');
            document.getElementById('modalBookPlaceholder').classList.add('hidden');
        } else {
            document.getElementById('modalBookCover').classList.add('hidden');
            document.getElementById('modalBookPlaceholder').classList.remove('hidden');
        }

        document.getElementById('bookDetailModal').classList.remove('hidden');
    } catch (error) {
        alert('Error loading book details: ' + error.message);
    }
}

function hideBookDetail() {
    document.getElementById('bookDetailModal').classList.add('hidden');
    currentBookId = null;
}

async function setStatus(status) {
    if (!currentBookId) return;

    try {
        const response = await fetch(`${API_URL}/books/${currentBookId}/status`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status })
        });

        if (!response.ok) throw new Error('Failed to update status');

        hideBookDetail();
        await loadDashboardData();

        const statusName = status === 'WANT_TO_READ' ? 'Want to Read' :
                          status === 'READING' ? 'Currently Reading' : 'Finished';
        alert(`Added to ${statusName}!`);
    } catch (error) {
        alert('Error updating status: ' + error.message);
    }
}

async function removeFromList(bookId) {
    if (!confirm('Remove this book from your list?')) return;

    try {
        const response = await fetch(`${API_URL}/books/${bookId}/status`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to remove book');

        // Refresh dashboard data immediately without alert
        await loadDashboardData();
    } catch (error) {
        alert('Error removing book: ' + error.message);
    }
}

function borrowBook() {
    if (!currentBookId) return;
    hideBookDetail();
    document.getElementById('borrowModal').classList.remove('hidden');
}

function hideBorrowModal() {
    document.getElementById('borrowModal').classList.add('hidden');
    document.getElementById('borrowerName').value = '';
    document.getElementById('borrowerContact').value = '';
    document.getElementById('borrowerNotes').value = '';
}

async function confirmBorrow(event) {
    event.preventDefault();

    const borrowData = {
        borrowerName: document.getElementById('borrowerName').value,
        borrowerContact: document.getElementById('borrowerContact').value || null,
        borrowerNotes: document.getElementById('borrowerNotes').value || null
    };

    try {
        const response = await fetch(`${API_URL}/books/${currentBookId}/borrow`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(borrowData)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to borrow book');
        }

        hideBorrowModal();
        await loadDashboardData();
        alert(`Book successfully lent to ${borrowData.borrowerName}!`);
    } catch (error) {
        alert('Error borrowing book: ' + error.message);
    }
}

async function returnBook() {
    if (!currentBookId) return;

    if (!confirm('Mark this book as returned?')) return;

    try {
        const response = await fetch(`${API_URL}/books/${currentBookId}/return`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to return book');
        }

        hideBookDetail();
        await loadDashboardData();
        alert('Book marked as returned successfully!');
    } catch (error) {
        alert('Error returning book: ' + error.message);
    }
}

async function addBook(event) {
    event.preventDefault();

    const bookData = {
        title: document.getElementById('bookTitle').value,
        author: document.getElementById('bookAuthor').value,
        genre: document.getElementById('bookGenre').value,
        description: document.getElementById('bookDescription').value,
        totalCount: 1
    };

    const formData = new FormData();
    formData.append('book', JSON.stringify(bookData));

    // Add image if provided
    const imageFile = document.getElementById('bookImage').files[0];
    if (imageFile) {
        formData.append('image', imageFile);
    }

    try {
        const response = await fetch(`${API_URL}/books`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${authToken}` },
            body: formData
        });

        if (!response.ok) throw new Error('Failed to add book');

        hideAddBook();
        event.target.reset();

        // Reload appropriate view
        const adminPanel = document.getElementById('adminPanel');
        if (!adminPanel.classList.contains('hidden')) {
            await loadAdminData();
        } else {
            await loadDashboardData();
        }

        alert('Book added successfully!');
    } catch (error) {
        alert('Error adding book: ' + error.message);
    }
}

// Admin Panel Functions
async function showAdminPage() {
    if (currentUser.role !== 'ADMIN') {
        alert('Access denied. Admin only.');
        return;
    }

    document.getElementById('authForms').classList.add('hidden');
    document.getElementById('dashboard').classList.add('hidden');
    document.getElementById('adminPanel').classList.remove('hidden');

    // Save current page
    localStorage.setItem('currentPage', 'admin');

    await loadAdminData();
}

async function loadAdminData() {
    await Promise.all([
        loadAnalytics(),
        loadAdminBooks(),
        loadAdminUsers(),
        loadAdminBorrowings()
    ]);
}

async function loadAnalytics() {
    try {
        const response = await fetch(`${API_URL}/admin/analytics`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to load analytics');

        const data = await response.json();
        document.getElementById('statTotalBooks').textContent = data.totalBooks || 0;
        document.getElementById('statTotalUsers').textContent = data.totalUsers || 0;
        document.getElementById('statBorrowed').textContent = data.currentlyBorrowedBooks || 0;
        document.getElementById('statActiveUsers').textContent = data.activeUsers || 0;
    } catch (error) {
        console.error('Error loading analytics:', error);
    }
}

async function loadAdminBooks() {
    try {
        const response = await fetch(`${API_URL}/books`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to load books');

        const books = await response.json();
        const tbody = document.getElementById('adminBooksTableBody');

        if (books.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="py-8 text-center text-text-secondary">No books yet</td></tr>';
            return;
        }

        tbody.innerHTML = books.map(book => `
            <tr class="border-b border-gray-100">
                <td class="py-3">${book.title}</td>
                <td class="py-3 text-text-secondary">${book.author}</td>
                <td class="py-3 text-text-secondary">${book.genre}</td>
                <td class="py-3">
                    <span class="${book.isAvailable ? 'text-green-600' : 'text-red-600'}">
                        ${book.availableCount}/${book.totalCount}
                    </span>
                </td>
                <td class="py-3">
                    <button onclick="deleteBook(${book.id})" class="text-red-600 hover:text-red-700 text-sm">
                        Delete
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading admin books:', error);
    }
}

async function loadAdminUsers() {
    try {
        const response = await fetch(`${API_URL}/admin/users`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to load users');

        const users = await response.json();
        const tbody = document.getElementById('adminUsersTableBody');

        tbody.innerHTML = users.map(user => `
            <tr class="border-b border-gray-100">
                <td class="py-3">${user.name}</td>
                <td class="py-3 text-text-secondary">${user.email}</td>
                <td class="py-3">
                    <span class="px-2 py-1 text-xs rounded-full ${user.role === 'ADMIN' ? 'bg-accent-light text-accent-brown' : 'bg-gray-200 text-gray-700'}">
                        ${user.role}
                    </span>
                </td>
                <td class="py-3">
                    <span class="${user.isActive ? 'text-green-600' : 'text-red-600'}">
                        ${user.isActive ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td class="py-3 text-text-secondary text-sm">
                    ${new Date(user.createdAt).toLocaleDateString()}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

async function loadAdminBorrowings() {
    try {
        const response = await fetch(`${API_URL}/admin/borrowings`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to load borrowings');

        const borrowings = await response.json();
        const tbody = document.getElementById('adminBorrowingsTableBody');

        if (borrowings.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="py-8 text-center text-text-secondary">No books currently borrowed</td></tr>';
            return;
        }

        tbody.innerHTML = borrowings.map(b => `
            <tr class="border-b border-gray-100">
                <td class="py-3">${b.bookTitle}</td>
                <td class="py-3 text-text-secondary">${b.userName}</td>
                <td class="py-3 text-text-secondary text-sm">
                    ${new Date(b.borrowedAt).toLocaleDateString()}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading borrowings:', error);
    }
}

function showAdminTab(tab) {
    // Save current admin tab
    localStorage.setItem('currentAdminTab', tab);

    // Update tab buttons
    document.getElementById('adminTabBooks').className = 'px-6 py-3 font-medium text-text-secondary hover:text-accent-brown border-b-2 border-transparent';
    document.getElementById('adminTabUsers').className = 'px-6 py-3 font-medium text-text-secondary hover:text-accent-brown border-b-2 border-transparent';
    document.getElementById('adminTabBorrowings').className = 'px-6 py-3 font-medium text-text-secondary hover:text-accent-brown border-b-2 border-transparent';

    // Update content
    document.getElementById('adminBooksTab').classList.add('hidden');
    document.getElementById('adminUsersTab').classList.add('hidden');
    document.getElementById('adminBorrowingsTab').classList.add('hidden');

    if (tab === 'books') {
        document.getElementById('adminTabBooks').className = 'px-6 py-3 font-medium text-accent-brown border-b-2 border-accent-brown';
        document.getElementById('adminBooksTab').classList.remove('hidden');
    } else if (tab === 'users') {
        document.getElementById('adminTabUsers').className = 'px-6 py-3 font-medium text-accent-brown border-b-2 border-accent-brown';
        document.getElementById('adminUsersTab').classList.remove('hidden');
    } else if (tab === 'borrowings') {
        document.getElementById('adminTabBorrowings').className = 'px-6 py-3 font-medium text-accent-brown border-b-2 border-accent-brown';
        document.getElementById('adminBorrowingsTab').classList.remove('hidden');
    }
}

async function deleteBook(bookId) {
    if (!confirm('Are you sure you want to delete this book?')) return;

    try {
        const response = await fetch(`${API_URL}/books/${bookId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Failed to delete book');

        // Refresh admin data immediately without alert
        await loadAdminData();
    } catch (error) {
        alert('Error deleting book: ' + error.message);
    }
}
