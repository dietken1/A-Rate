declare module "react-rating-stars-component" {
  import * as React from "react";

  export interface ReactStarsProps {
    count?: number;
    value?: number;
    onChange?: (newValue: number) => void;
    size?: number;
    color?: string;
    activeColor?: string;
    a11y?: boolean;
    isHalf?: boolean;
    edit?: boolean;
    emptyIcon?: React.ReactNode;
    halfIcon?: React.ReactNode;
    filledIcon?: React.ReactNode;
    [key: string]: any;
  }

  const ReactStars: React.FC<ReactStarsProps>;
  export default ReactStars;
}
