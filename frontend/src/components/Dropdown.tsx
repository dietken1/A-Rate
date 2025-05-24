import { useState, useEffect, useRef } from "react"; // useEffect와 useRef를 import합니다.

interface DropdownProps {
  options: string[];
  value?: string;
  onChange?: (value: string) => void;
  placeholder?: string;
}

const Dropdown = ({ options, value, onChange, placeholder }: DropdownProps) => {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null); // 드롭다운 DOM 요소에 대한 ref 생성

  const handleSelect = (option: string) => {
    onChange?.(option);
    setOpen(false);
  };

  // 1. 드롭다운 외부 클릭 시 닫힘 처리
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      // dropdownRef.current가 존재하고, 클릭된 대상(event.target)이
      // dropdownRef.current 내부에 포함되어 있지 않은 경우 드롭다운을 닫습니다.
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    };

    // 드롭다운이 열려 있을 때만 mousedown 이벤트 리스너를 추가합니다.
    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    // 클린업 함수: 컴포넌트가 언마운트되거나 open 상태가 변경되기 전에 실행되어
    // 이벤트 리스너를 제거합니다.
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [open]); // open 상태가 변경될 때마다 이 useEffect 훅을 다시 실행합니다.

  return (
    <div
      className="relative inline-block w-48"
      ref={dropdownRef}
      style={{ zIndex: 50 }}
    >
      {" "}
      {/* ref 할당 */}
      <button
        type="button"
        className="flex items-center justify-between w-full px-4 py-2 text-sm text-left transition duration-200 bg-white border border-gray-300 rounded-full focus:outline-none focus:border-primary" // 애니메이션 지속시간을 200ms로 변경
        onClick={() => setOpen((prev) => !prev)}
        aria-haspopup="listbox"
        aria-expanded={open}
      >
        <span className="text-sm truncate max-w-[8rem] block">
          {value || placeholder || "선택하세요"}
        </span>
        <svg
          className={`w-4 h-4 ml-2 transition-transform duration-fast ${
            open ? "rotate-180" : "rotate-0"
          }`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M19 9l-7 7-7-7"
          />
        </svg>
      </button>
      {/* 2. 부드러운 열림/닫힘 애니메이션 적용 */}
      <ul
        className={`absolute z-50 w-full mt-1 bg-white border border-gray-200 rounded-md shadow-lg 
                    text-sm origin-top
                    transition-all duration-200 ease-in-out
                    ${
                      open
                        ? "opacity-100 transform scale-y-100 max-h-60 overflow-auto visible"
                        : "opacity-0 transform scale-y-95 max-h-0 overflow-hidden pointer-events-none invisible"
                    }`}
        role="listbox"
        aria-hidden={!open} // 접근성을 위해 open 상태에 따라 aria-hidden 설정
      >
        {options.map((option) => (
          <li
            key={option}
            className={`px-4 py-2 cursor-pointer hover:bg-grayscale-200 transition duration-fast z-50 ${
              // 호버 효과는 기존 duration-fast (150ms) 유지 또는 200ms로 통일 가능
              option === value ? "bg-primary text-white" : ""
            } text-sm`}
            onClick={() => handleSelect(option)}
            role="option"
            aria-selected={option === value}
          >
            <span className="truncate block max-w-[7.5rem]">{option}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Dropdown;
