# iyal Frontend Setup Guide

## Quick Start

### 1. Create Next.js Project

```bash
# Navigate to your workspace
cd ~/Downloads

# Create Next.js app with TypeScript and Tailwind
npx create-next-app@latest iyal-frontend --typescript --tailwind --app --src-dir --import-alias "@/*"

cd iyal-frontend
```

### 2. Install Dependencies

```bash
# Core dependencies
npm install axios zustand @tanstack/react-query
npm install framer-motion lucide-react
npm install clsx tailwind-merge class-variance-authority
npm install date-fns

# shadcn/ui setup
npx shadcn-ui@latest init

# Install shadcn components (select defaults)
npx shadcn-ui@latest add button
npx shadcn-ui@latest add input
npx shadcn-ui@latest add card
npx shadcn-ui@latest add dialog
npx shadcn-ui@latest add dropdown-menu
npx shadcn-ui@latest add form
npx shadcn-ui@latest add label
npx shadcn-ui@latest add select
npx shadcn-ui@latest add textarea
npx shadcn-ui@latest add toast
npx shadcn-ui@latest add tabs
npx shadcn-ui@latest add avatar
npx shadcn-ui@latest add badge
npx shadcn-ui@latest add skeleton
npx shadcn-ui@latest add alert
```

### 3. Project Structure

```
iyal-frontend/
├── src/
│   ├── app/
│   │   ├── (auth)/
│   │   │   ├── login/
│   │   │   └── register/
│   │   ├── (dashboard)/
│   │   │   ├── layout.tsx
│   │   │   ├── page.tsx (home dashboard)
│   │   │   ├── books/
│   │   │   ├── my-books/
│   │   │   ├── profile/
│   │   │   └── settings/
│   │   ├── (admin)/
│   │   │   ├── layout.tsx
│   │   │   ├── admin/
│   │   │   │   ├── dashboard/
│   │   │   │   ├── books/
│   │   │   │   ├── users/
│   │   │   │   └── analytics/
│   │   ├── layout.tsx
│   │   ├── page.tsx (landing/redirect)
│   │   └── globals.css
│   ├── components/
│   │   ├── ui/ (shadcn components)
│   │   ├── layout/
│   │   │   ├── Navbar.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   └── Footer.tsx
│   │   ├── books/
│   │   │   ├── BookCard.tsx
│   │   │   ├── BookGrid.tsx
│   │   │   ├── BookDetail.tsx
│   │   │   └── BookFilters.tsx
│   │   ├── dashboard/
│   │   │   ├── ContinueReading.tsx
│   │   │   ├── FamilyReading.tsx
│   │   │   ├── RecentBooks.tsx
│   │   │   └── PopularBooks.tsx
│   │   ├── profile/
│   │   │   ├── UserProfile.tsx
│   │   │   └── ReadingStats.tsx
│   │   └── admin/
│   │       ├── AnalyticsCard.tsx
│   │       ├── UserTable.tsx
│   │       └── BookForm.tsx
│   ├── lib/
│   │   ├── api.ts (axios instance)
│   │   ├── utils.ts
│   │   └── types.ts
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useBooks.ts
│   │   └── useDashboard.ts
│   ├── store/
│   │   └── authStore.ts (Zustand)
│   └── providers/
│       └── Providers.tsx (React Query)
```

### 4. Environment Variables

Create `.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

### 5. Tailwind Configuration for Aesthetics

Update `tailwind.config.ts`:

```typescript
import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        // Warm, minimal palette
        bg: {
          primary: '#FEFCF8',      // Warm off-white
          secondary: '#F9F6F1',    // Slightly darker warm
        },
        accent: {
          brown: '#8B7355',        // Muted brown
          light: '#C4B5A0',        // Light brown
        },
        text: {
          primary: '#2D2A26',      // Charcoal
          secondary: '#6B6662',    // Soft gray
          muted: '#9E9A95',        // Very soft gray
        }
      },
      fontFamily: {
        serif: ['Playfair Display', 'serif'],
        sans: ['Inter', 'sans-serif'],
      },
      borderRadius: {
        'card': '12px',
      },
      boxShadow: {
        'soft': '0 2px 8px rgba(0, 0, 0, 0.04)',
        'medium': '0 4px 16px rgba(0, 0, 0, 0.08)',
      }
    },
  },
  plugins: [],
}
export default config
```

### 6. Google Fonts Setup

Update `src/app/layout.tsx`:

```typescript
import { Inter, Playfair_Display } from 'next/font/google'

const inter = Inter({ 
  subsets: ['latin'],
  variable: '--font-inter',
})

const playfair = Playfair_Display({ 
  subsets: ['latin'],
  variable: '--font-playfair',
})

// In className: `${inter.variable} ${playfair.variable}`
```

### 7. Run Development Server

```bash
npm run dev
```

Visit `http://localhost:3000`

## Design Principles

1. **Whitespace is your friend** - Let content breathe
2. **Typography hierarchy** - Serif for headings, sans-serif for body
3. **Soft shadows** - Avoid harsh borders
4. **Warm neutrals** - Create cozy feeling
5. **Subtle animations** - Use framer-motion sparingly
6. **Large images** - Book covers should be prominent
7. **Clean navigation** - Minimal, elegant
8. **Responsive first** - Mobile to desktop

## Key Pages Overview

### Landing/Auth Pages
- Clean, centered forms
- Subtle gradient backgrounds
- Large typography
- Minimal distractions

### Dashboard
- Card-based layout
- Grid of book covers
- Personalized sections
- Activity feed

### Browse Books
- Filterable grid
- Large book covers
- Search with instant results
- Genre chips

### Book Detail
- Hero image
- Clean typography
- Action buttons (borrow, reading status)
- Currently reading list

### User Profile
- Avatar
- Reading statistics
- Currently reading
- Recently finished
- Toggle visibility

### Admin Dashboard
- Analytics cards
- Tables with actions
- Book form with image upload
- User management

## Next Steps

1. Copy the complete frontend code (see FRONTEND_CODE.md)
2. Install all dependencies
3. Configure environment variables
4. Start the dev server
5. Test with backend running

## Deployment Ready

Frontend is configured for:
- Vercel (recommended)
- Netlify
- Any static host

Just update `NEXT_PUBLIC_API_URL` to production backend URL.

---

**Note:** The complete frontend implementation with all components, pages, and styling is provided in separate files due to size. Follow this setup guide first, then integrate the components.
