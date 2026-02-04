function Divider() {
  return (
    <div className="relative my-10">
      <div className="absolute inset-0 flex items-center">
        <div className="w-full border-t border-slate-200 dark:border-slate-800" />
      </div>
      <div className="relative flex justify-center text-sm uppercase">
        <span className="bg-white px-4 font-medium text-slate-400 dark:bg-background-dark">
          Or continue with
        </span>
      </div>
    </div>
  );
}
export default Divider;