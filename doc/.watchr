watch(/bench\.tex/) do |match|
  cmd = "latex --output-format=pdf bench.tex"#" && open bench.pdf"
  puts "Running #{cmd}"
  system(cmd)
end
