function Field({
  id,
  label,
  type,
  placeholder,
  icon: Icon,
  value,
  onChange,
}: {
  id: string;
  label: string;
  type: string;
  placeholder?: string;
  icon: React.ElementType;
  value: string;
  onChange: (v: string) => void;
}) {
  return (
    <div className="space-y-2">
      <label
        htmlFor={id}
        className="text-sm font-semibold text-[#0d141b] dark:text-slate-200"
      >
        {label}
      </label>
      <div className="relative">
        <Icon className="absolute left-3 top-1/2 size-5 -translate-y-1/2 text-slate-400" />
        <input
          id={id}
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full rounded-lg border border-slate-200 bg-slate-50 py-3.5 pl-10 pr-4 text-[#0d141b]
           placeholder:text-slate-400 transition-all focus:border-primary focus:outline-none focus:ring-2
           focus:ring-primary/20 dark:border-slate-700 dark:bg-slate-800/50 dark:text-white"
        />
      </div>
    </div>
  );
}
export default Field;