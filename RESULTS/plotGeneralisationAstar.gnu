set key bottom right
plot [:125000][33000:55000] "./single-Astar_avg_best_ind_per_obj.dat" u 1:2 every ::1 w lp, "./five-Astar_avg_best_ind_per_obj.dat" u 1:2 every ::1 w lp, "./change-Astar_avg_best_ind_per_obj.dat" u 1:2 every ::1 w lp, "./slide-Astar_avg_best_ind_per_obj.dat" u 1:2 every ::1 w lp, "./pathFollower-Astar_avg_best_ind_per_obj.dat" u 1:2 w l, "./change5-Astar_avg_best_ind_per_obj.dat" u 1:2 every ::1 w lp

