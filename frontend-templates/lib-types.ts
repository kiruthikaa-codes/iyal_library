// src/lib/types.ts

export interface User {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
  isProfilePublic: boolean;
  isActive: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface Book {
  id: number;
  title: string;
  author: string;
  genre: string;
  description?: string;
  coverImageUrl?: string;
  totalCount: number;
  availableCount: number;
  createdAt: string;
  isAvailable: boolean;
}

export interface ReadingStatus {
  id: number;
  userId: number;
  userName: string;
  bookId: number;
  bookTitle: string;
  bookCoverImageUrl?: string;
  bookAuthor: string;
  status: 'WANT_TO_READ' | 'READING' | 'FINISHED';
  updatedAt: string;
}

export interface Borrowing {
  id: number;
  userId: number;
  userName: string;
  bookId: number;
  bookTitle: string;
  bookCoverImageUrl?: string;
  borrowedAt: string;
  returnedAt?: string;
  isReturned: boolean;
}

export interface UserProfile {
  id: number;
  name: string;
  isProfilePublic: boolean;
  createdAt: string;
  currentlyReading: ReadingStatus[];
  recentlyFinished: ReadingStatus[];
  totalBooksRead: number;
}

export interface Dashboard {
  continueReading: ReadingStatus[];
  currentlyReadingByFamily: ReadingStatus[];
  recentlyAdded: Book[];
  popularBooks: Book[];
}

export interface Analytics {
  totalBooks: number;
  totalUsers: number;
  currentlyBorrowedBooks: number;
  activeUsers: number;
  popularGenres: Record<string, number>;
  mostReadBooks: Record<string, number>;
}

export interface BookFilters {
  title?: string;
  author?: string;
  genre?: string;
  available?: boolean;
}
