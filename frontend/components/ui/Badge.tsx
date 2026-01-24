'use client';

import type {ElementType} from 'react';

const Badge = ({
  icon: Icon,
  label,
}: {
  icon: ElementType;
  label: string;
}) => {
  return (
    <div className="flex items-center gap-2 rounded-full border border-white/20 bg-white/10 px-4 py-2 backdrop-blur-md">
      <Icon className="size-4 text-white" />
      <span className="text-sm font-semibold text-white">{label}</span>
    </div>
  );
};

export default Badge;