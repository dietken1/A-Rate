const Logo = ({ className }: { className?: string }) => (
  <div className={`flex gap-2 ${className} items-center`}>
    <img src="/icons/icon.svg" alt="Logo" className="w-[32px] h-[32px]" />
    <h1 className="text-md font-bold">A:Rate</h1>
  </div>
);

export default Logo;
