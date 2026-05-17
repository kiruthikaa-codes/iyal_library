// src/components/books/BookCard.tsx
'use client';

import { motion } from 'framer-motion';
import { Book as BookIcon, User, BookOpen } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';
import type { Book } from '@/lib/types';
import { Badge } from '@/components/ui/badge';

interface BookCardProps {
  book: Book;
  showActions?: boolean;
}

export default function BookCard({ book, showActions = true }: BookCardProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="group"
    >
      <Link href={`/books/${book.id}`}>
        <div className="bg-white rounded-card shadow-soft hover-lift overflow-hidden h-full flex flex-col">
          {/* Book Cover */}
          <div className="relative aspect-[2/3] bg-bg-secondary overflow-hidden">
            {book.coverImageUrl ? (
              <Image
                src={book.coverImageUrl}
                alt={book.title}
                fill
                className="object-cover transition-transform duration-300 group-hover:scale-105"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center">
                <BookIcon className="w-16 h-16 text-text-muted" />
              </div>
            )}
            
            {/* Availability Badge */}
            <div className="absolute top-3 right-3">
              <Badge 
                variant={book.isAvailable ? 'default' : 'secondary'}
                className={book.isAvailable ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'}
              >
                {book.isAvailable ? 'Available' : 'Borrowed'}
              </Badge>
            </div>
          </div>

          {/* Book Info */}
          <div className="p-4 flex-1 flex flex-col">
            <h3 className="font-serif text-lg font-semibold text-text-primary mb-1 line-clamp-2 group-hover:text-accent-brown transition-colors">
              {book.title}
            </h3>
            
            <p className="text-sm text-text-secondary mb-2 flex items-center gap-1">
              <User className="w-3 h-3" />
              {book.author}
            </p>
            
            <p className="text-xs text-text-muted mb-3">
              {book.genre}
            </p>
            
            {book.description && (
              <p className="text-sm text-text-secondary line-clamp-2 mb-3">
                {book.description}
              </p>
            )}
            
            <div className="mt-auto flex items-center justify-between text-xs text-text-muted">
              <span className="flex items-center gap-1">
                <BookOpen className="w-3 h-3" />
                {book.availableCount} of {book.totalCount} available
              </span>
            </div>
          </div>
        </div>
      </Link>
    </motion.div>
  );
}
