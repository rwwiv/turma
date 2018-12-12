-- Initialization:
{states: S,A,R}
{start: S}
{accept: A}
{reject: R}
{alpha: 0,1}
{tape-alpha: 0,1}
-- Main Algorithm:
-- 0:

rwRt S 0 1 S;
rwRt S 1 0 S;
rwRt S _ _ S;
