------------------------------------------------------------------------
This is the project README file. Here, you should describe your project.
Tell the reader (someone who does not know anything about this project)
all he/she needs to know. The comments should usually include at least:
------------------------------------------------------------------------

PROJECT TITLE:
PURPOSE OF PROJECT:
VERSION or DATE:
HOW TO START THIS PROJECT:
AUTHORS:
USER INSTRUCTIONS:


    private static double parseDouble(String s) throws Exception
    {
        double ret = 0;
        double place = 0.1;

        boolean afterDecimal = false;
        for (int i = 0; i < s.length(); i++)
        {
            String c = s.substring(i, i+1);

            if (isDigit(s))
            {
                if (!afterDecimal)
                {
                    ret = ret * 10 + valueOf(c);
                }
                else
                {
                    ret += valueOf(c) * place;
                    place /= 10;
                }
            }
            else if (c.equals(".") && !afterDecimal)
            {
                afterDecimal = true;
            }
            else
            {
                throw new Exception(s + " is not a number ya idiot!");
            }
        }
        return ret;
    }
