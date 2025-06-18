import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

// 1. Specify allowed origins
const allowedOrigins = [
  "https://students.washington.edu",
  "https://rotartsi82.github.io",
  "https://rotartsi.vercel.app",
  "https://granty.netlify.app"
];

const corsOptions = {
  'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
  'Access-Control-Allow-Headers': 'Content-Type, Authorization',
  'Access-Control-Allow-Credentials': 'true'
};

export function middleware(request: NextRequest) {
  // 2. Check the origin from the request
  const origin = request.headers.get('origin') ?? '';
  const isAllowedOrigin = allowedOrigins.includes(origin.trim().toLowerCase());

  // 3. Handle preflight requests
  const isPreflight = request.method === 'OPTIONS';

  if (isPreflight) {
    const preflightHeaders = {
      ...(isAllowedOrigin && { 'Access-Control-Allow-Origin': origin }),
      ...corsOptions,
    };
    return NextResponse.json({}, { headers: preflightHeaders });
  }

  // 4. Handle simple requests
  const response = NextResponse.next();

  if (isAllowedOrigin) {
    response.headers.set('Access-Control-Allow-Origin', origin);
  }

  // Add the remaining CORS headers to the response
  Object.entries(corsOptions).forEach(([key, value]) => {
    response.headers.set(key, value);
  });

  return response;
}

// 5. Specify which paths this middleware should run on
export const config = {
  matcher: '/api/:path*',
};
