import React from "react";

// Có thể truyền props: name, avatarUrl
function ProfileBanner({ name = "User", avatarUrl }) {
  return (
    <svg
      width="100%"
      height="300"
      viewBox="0 0 1200 300"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      style={{ borderRadius: 32, boxShadow: '0 8px 32px 0 rgba(0,0,0,0.18)' }}
      className="overflow-hidden"
    >
      {/* Gradient background */}
      <defs>
        <linearGradient id="bg-gradient" x1="0" y1="0" x2="1200" y2="300" gradientUnits="userSpaceOnUse">
          <stop stopColor="#e4d804" />
          <stop offset="1" stopColor="#232526" />
        </linearGradient>
        <radialGradient id="avatar-glow" cx="50%" cy="50%" r="60%">
          <stop offset="0%" stopColor="#fffbe6" stopOpacity="0.7" />
          <stop offset="100%" stopColor="#e4d804" stopOpacity="0.1" />
        </radialGradient>
      </defs>
      <rect x="0" y="0" width="1200" height="300" rx="32" fill="url(#bg-gradient)" />
      {/* Abstract waves/dots */}
      <ellipse cx="600" cy="220" rx="420" ry="60" fill="#fff" fillOpacity="0.07" />
      <ellipse cx="600" cy="250" rx="350" ry="30" fill="#fff" fillOpacity="0.04" />
      <circle cx="300" cy="80" r="18" fill="#fff" fillOpacity="0.08" />
      <circle cx="900" cy="60" r="12" fill="#fff" fillOpacity="0.10" />
      {/* Avatar glow */}
      <circle cx="600" cy="150" r="74" fill="url(#avatar-glow)" />
      {/* Avatar placeholder */}
      <circle cx="600" cy="150" r="60" fill="#fff" filter="url(#avatarShadow)" />
      {avatarUrl ? (
        <image
          href={avatarUrl}
          x="540"
          y="90"
          width="120"
          height="120"
          clipPath="circle(60px at 60px 60px)"
        />
      ) : (
        <text
          x="600"
          y="170"
          textAnchor="middle"
          fontSize="56"
          fontWeight="bold"
          fill="#e4d804"
          style={{ fontFamily: 'sans-serif', letterSpacing: 2 }}
        >
          {name?.trim()?.charAt(0)?.toUpperCase() || 'U'}
        </text>
      )}
      {/* Welcoming text */}
      <text
        x="600"
        y="260"
        textAnchor="middle"
        fontSize="44"
        fontWeight="bold"
        fill="#fff"
        style={{ fontFamily: 'sans-serif', textShadow: '0 2px 8px #232526' }}
      >
        Chào mừng trở lại, {name}!
      </text>
      {/* Movie-themed icons (star, ticket, camera) bottom right */}
      <g opacity="0.18">
        {/* Star */}
        <polygon points="1120,260 1127,278 1146,278 1131,289 1137,307 1120,296 1103,307 1109,289 1094,278 1113,278" fill="#fff" />
        {/* Ticket */}
        <rect x="1150" y="250" width="36" height="24" rx="6" fill="#fff" />
        <circle cx="1156" cy="262" r="2" fill="#e4d804" />
        <circle cx="1178" cy="262" r="2" fill="#e4d804" />
        {/* Camera */}
        <rect x="1080" y="255" width="24" height="16" rx="4" fill="#fff" />
        <rect x="1102" y="259" width="8" height="8" rx="2" fill="#fff" />
        <rect x="1086" y="259" width="8" height="8" rx="2" fill="#e4d804" />
      </g>
    </svg>
  );
}

export default ProfileBanner; 